package dev.hireben.spring.reactive.web.demo.storage.service.api;

import dev.hireben.spring.reactive.web.demo.storage.entity.File;
import reactor.core.publisher.Mono;

public interface FileService {

  Mono<File> info(File file);

  Mono<File> download(File file);

  Mono<File> upload(File file);

  Mono<File> replace(File file);

  Mono<Void> remove(File file);

}
