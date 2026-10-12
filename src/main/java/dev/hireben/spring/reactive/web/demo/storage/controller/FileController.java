package dev.hireben.spring.reactive.web.demo.storage.controller;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;

import dev.hireben.spring.reactive.web.demo.common.annotation.api.AuthorizationHeader;
import dev.hireben.spring.reactive.web.demo.common.dto.Client;
import dev.hireben.spring.reactive.web.demo.common.exception.OperationDeniedException;
import dev.hireben.spring.reactive.web.demo.storage.entity.File;
import dev.hireben.spring.reactive.web.demo.storage.service.api.FileService;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.FileNotFoundException;
import java.util.Optional;

import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
@RequestMapping("/files/{id}")
@RequiredArgsConstructor
final class FileController {

  private final FileService service;

  @GetMapping
  Mono<ResponseEntity<Flux<DataBuffer>>> downloadHandler(
      @PathVariable String id,
      @AuthorizationHeader Client client,
      ServerWebExchange exchange) {
    return service.info(id)
        .doOnNext(current -> exchange.getResponse().getHeaders().setCacheControl(current.getCacheControl()))
        .filter(current -> !exchange.checkNotModified(current.getETag(), current.getLastModified()))
        .map(current -> ResponseEntity
            .ok()
            .contentType(current.getContentType())
            .contentLength(current.getContentLength())
            .body(exchange.getRequest().getMethod() == HttpMethod.HEAD
                ? Flux.empty()
                : service.download(id, current.getETag())));
  }

  @DeleteMapping
  Mono<ResponseEntity<Void>> deleteHandler(
      @PathVariable String id,
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

  @PutMapping
  Mono<ResponseEntity<Void>> uploadHandler(
      @PathVariable String id,
      @AuthorizationHeader Client client,
      @RequestBody Flux<DataBuffer> content,
      ServerWebExchange exchange) {
    // If-Match: eTag -> update
    // If-None-Match: * -> eTag -> create
    return service.info(id)
        .map(Optional::of)
        .onErrorReturn(FileNotFoundException.class, Optional.empty())
        .filter(current -> current.map(f -> f.getOwner().equals(client.id())).orElse(true))
        .switchIfEmpty(Mono.error(OperationDeniedException::new))
        .flatMap(current -> {
          boolean exists = current.isPresent();
          boolean failed = exists
              ? exchange.checkNotModified(current.get().getETag(), current.get().getLastModified())
              : !exchange.getRequest().getHeaders().getIfMatch().isEmpty();
          if (failed) {
            return Mono.just(ResponseEntity.status(HttpStatus.PRECONDITION_FAILED).<Void>build());
          }
          return service
              .upload(id, client.id(), exchange.getRequest().getHeaders().getContentType(), content,
                  current.map(File::getETag).orElse(null))
              .map(saved -> (exists
                  ? ResponseEntity.noContent()
                  : ResponseEntity.created(exchange.getRequest().getURI()))
                  .eTag(saved.getETag())
                  .lastModified(saved.getLastModified())
                  .build());
        });
  }

}
