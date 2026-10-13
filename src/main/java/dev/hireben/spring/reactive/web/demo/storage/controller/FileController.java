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
import java.time.Instant;

import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
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
            .contentType(MediaType.parseMediaType(current.getContentType()))
            .contentLength(current.getContentLength())
            .body(exchange.getRequest().getMethod() == HttpMethod.HEAD
                ? Flux.empty()
                : service.download(id, current.getETag())));
  }

  @PutMapping
  Mono<ResponseEntity<Void>> uploadHandler(
      @PathVariable String id,
      @AuthorizationHeader Client client,
      @RequestBody Flux<DataBuffer> content,
      ServerWebExchange exchange) {
    return service.info(id)
        .filter(current -> current.getOwner().equals(client.id()))
        .switchIfEmpty(Mono.error(OperationDeniedException::new))
        .filter(current -> !exchange.checkNotModified(current.getETag(), current.getLastModified()))
        .flatMap(current -> service.upload(
            current.toBuilder()
                .contentType(exchange.getRequest().getHeaders().getContentType().toString())
                .contentLength(exchange.getRequest().getHeaders().getContentLength())
                .cacheControl(exchange.getRequest().getHeaders().getCacheControl())
                .build(),
            content)
            .map(file -> ResponseEntity
                .noContent()
                .eTag(file.getETag())
                .lastModified(file.getLastModified())
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
                .lastModified(file.getLastModified())
                .build()));
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

}
