package dev.hireben.spring.reactive.web.demo.storage.repository.api;

import dev.hireben.spring.reactive.web.demo.common.dto.Precondition;
import dev.hireben.spring.reactive.web.demo.storage.entity.File;
import reactor.core.publisher.Mono;

public interface FileRepository {
  Mono<File> head(String id, Precondition precondition);

  Mono<File> load(String id, Precondition precondition);

  Mono<File> save(File file, Precondition precondition);

  Mono<Void> delete(String id, Precondition precondition);
}
