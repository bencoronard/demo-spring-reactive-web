package dev.hireben.spring.reactive.web.demo.storage.service.api;

import org.springframework.core.io.buffer.DataBuffer;

import dev.hireben.spring.reactive.web.demo.storage.entity.File;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface FileService {

  Mono<File> info(String id);

  Flux<DataBuffer> download(String id, String eTag);

  Mono<File> upload(File file, Flux<DataBuffer> content);

  Mono<File> replace(File file, Flux<DataBuffer> content);

  Mono<Void> remove(String id, String eTag);

}
