package dev.hireben.spring.reactive.web.demo.storage.service.api;

import java.nio.ByteBuffer;

import dev.hireben.spring.reactive.web.demo.storage.entity.File;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface FileService {

  Mono<File> info(String id);

  Flux<ByteBuffer> download(String id, String eTag);

  Mono<File> upload(File file, Flux<ByteBuffer> content);

  Mono<Void> remove(String id, String eTag);

}
