package dev.hireben.spring.reactive.web.demo.storage.controller;

import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
@RequestMapping("/files/{id}")
@RequiredArgsConstructor
final class FileController {

  @RequestMapping(method = RequestMethod.HEAD)
  Mono<ResponseEntity<Flux<DataBuffer>>> infoHandler(
      @PathVariable String id) {
    // If-None-Match: eTag
    // If-Modified-Since: lastModified
    return Mono.just(ResponseEntity
        .ok()
        .build());
  }

  @GetMapping
  Mono<ResponseEntity<Flux<DataBuffer>>> downloadHandler(
      @PathVariable String id) {
    // If-None-Match: eTag
    // If-Modified-Since: lastModified
    return Mono.just(ResponseEntity
        .ok()
        .build());
  }

  @PutMapping
  Mono<ResponseEntity<Void>> uploadHandler(
      @PathVariable String id,
      @RequestBody Flux<DataBuffer> body) {
    // If-Match: eTag -> update
    // If-None-Match: * -> eTag -> create
    return Mono.just(ResponseEntity
        .noContent()
        .build());
  }

  @DeleteMapping
  Mono<ResponseEntity<Void>> deleteHandler(
      @PathVariable String id) {
    // If-Match: eTag
    return Mono.just(ResponseEntity
        .noContent()
        .build());
  }

}
