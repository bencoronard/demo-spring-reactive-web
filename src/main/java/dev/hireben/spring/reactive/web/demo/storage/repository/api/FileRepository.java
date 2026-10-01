package dev.hireben.spring.reactive.web.demo.storage.repository.api;

import java.nio.ByteBuffer;

import dev.hireben.spring.reactive.web.demo.storage.entity.File;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface FileRepository {

  Mono<File> head(String id);

  Flux<ByteBuffer> load(String id, String eTag);

  Mono<File> save(File file, Flux<ByteBuffer> content);

  Mono<Void> delete(String id, String eTag);

}
