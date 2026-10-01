package dev.hireben.spring.reactive.web.demo.storage.repository.api;

import org.springframework.core.io.buffer.DataBuffer;

import dev.hireben.spring.reactive.web.demo.storage.entity.File;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface FileRepository {

  Mono<File> head(File file);

  Flux<DataBuffer> load(File file);

  Mono<File> save(File file);

  Mono<File> update(File file);

  Mono<Void> delete(File file);

}
