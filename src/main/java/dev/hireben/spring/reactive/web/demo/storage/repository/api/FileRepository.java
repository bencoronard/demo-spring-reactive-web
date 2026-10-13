package dev.hireben.spring.reactive.web.demo.storage.repository.api;

import org.springframework.core.io.buffer.DataBuffer;

import dev.hireben.spring.reactive.web.demo.storage.entity.File;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface FileRepository {

  Mono<File> head(String id);

  Flux<DataBuffer> load(String id, String eTag);

  Mono<File> save(File file, Flux<DataBuffer> content);

  Mono<Void> delete(String id, String eTag);

}
