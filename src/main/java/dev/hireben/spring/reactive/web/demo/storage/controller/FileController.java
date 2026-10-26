package dev.hireben.spring.reactive.web.demo.storage.controller;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import dev.hireben.spring.reactive.web.demo.common.annotation.api.AuthorizationHeader;
import dev.hireben.spring.reactive.web.demo.common.dto.Client;
import dev.hireben.spring.reactive.web.demo.common.exception.MissingRequiredHeadersException;
import dev.hireben.spring.reactive.web.demo.common.exception.OperationDeniedException;
import dev.hireben.spring.reactive.web.demo.storage.annotation.api.ValidFileId;
import dev.hireben.spring.reactive.web.demo.storage.entity.File;
import dev.hireben.spring.reactive.web.demo.storage.exception.FileNotFoundException;
import dev.hireben.spring.reactive.web.demo.storage.exception.FileSizeTooLargeException;
import dev.hireben.spring.reactive.web.demo.storage.exception.FileTypeNotSupportedException;
import dev.hireben.spring.reactive.web.demo.storage.property.StorageProperties;
import dev.hireben.spring.reactive.web.demo.storage.service.api.FileService;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.nio.ByteBuffer;
import java.time.Instant;
import java.util.List;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;

@Validated
@RestController
@RequestMapping("/files/{id}")
@RequiredArgsConstructor
class FileController {

  private final StorageProperties properties;
  private final FileService service;

  @GetMapping
  Mono<ResponseEntity<Flux<ByteBuffer>>> downloadHandler(
      @PathVariable @ValidFileId String id,
      @AuthorizationHeader Client client,
      ServerWebExchange exchange) {
    return service.info(id)
        .doOnNext(current -> exchange.getResponse().getHeaders().setCacheControl(current.getCacheControl()))
        .filter(current -> !exchange.checkNotModified(current.getETag(), current.getLastModified()))
        .map(current -> ResponseEntity
            .ok()
            .contentType(MediaType.parseMediaType(current.getContentType()))
            .contentLength(current.getContentLength())
            .body(exchange.getRequest().getMethod() == HttpMethod.HEAD
                ? Flux.empty()
                : service.download(id, current.getETag())));
  }

  @PutMapping
  Mono<ResponseEntity<Void>> uploadHandler(
      @PathVariable @ValidFileId String id,
      @AuthorizationHeader Client client,
      @RequestBody Flux<ByteBuffer> content,
      ServerWebExchange exchange) {

    MediaType contentType = exchange.getRequest().getHeaders().getContentType();
    if (contentType == null) {
      throw new MissingRequiredHeadersException(List.of(HttpHeaders.CONTENT_TYPE));
    }
    if (!properties.file().upload().contentTypesAllowed().contains(contentType)
        && !properties.file().upload().contentGroupsAllowed().contains(contentType.getType())) {
      throw new FileTypeNotSupportedException(contentType);
    }

    long contentLength = exchange.getRequest().getHeaders().getContentLength();
    if (contentLength <= 0) {
      throw new MissingRequiredHeadersException(List.of(HttpHeaders.CONTENT_LENGTH));
    }
    if (contentLength > properties.file().upload().contentMaxLength()) {
      throw new FileSizeTooLargeException(properties.file().upload().contentMaxLength(), contentLength);
    }

    return service.info(id)
        .filter(current -> current.getOwner().equals(client.id()))
        .switchIfEmpty(Mono.error(OperationDeniedException::new))
        .filter(current -> !exchange.checkNotModified(current.getETag(), current.getLastModified()))
        .flatMap(current -> service.upload(
            current.toBuilder()
                .contentType(contentType.toString())
                .contentLength(contentLength)
                .cacheControl(exchange.getRequest().getHeaders().getCacheControl())
                .build(),
            content)
            .map(file -> ResponseEntity
                .noContent()
                .eTag(file.getETag())
                .<Void>build()))
        .onErrorResume(FileNotFoundException.class, ex -> service.upload(
            File.builder()
                .id(id)
                .owner(client.id())
                .createdAt(Instant.now())
                .contentType(exchange.getRequest().getHeaders().getContentType().toString())
                .contentLength(exchange.getRequest().getHeaders().getContentLength())
                .cacheControl(exchange.getRequest().getHeaders().getCacheControl())
                .build(),
            content)
            .map(file -> ResponseEntity
                .created(exchange.getRequest().getURI())
                .eTag(file.getETag())
                .build()));
  }

  @DeleteMapping
  Mono<ResponseEntity<Void>> deleteHandler(
      @PathVariable @ValidFileId String id,
      @AuthorizationHeader Client client,
      ServerWebExchange exchange) {
    return service.info(id)
        .filter(current -> current.getOwner().equals(client.id()))
        .switchIfEmpty(Mono.error(OperationDeniedException::new))
        .filter(current -> !exchange.checkNotModified(current.getETag(), current.getLastModified()))
        .flatMap(current -> service.remove(id, current.getETag()).thenReturn(true))
        .onErrorReturn(FileNotFoundException.class, true)
        .map(removed -> ResponseEntity.noContent().build());
  }

}
