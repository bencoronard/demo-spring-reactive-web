package dev.hireben.spring.reactive.web.demo.storage.service.api;

import dev.hireben.spring.reactive.web.demo.common.dto.Precondition;
import dev.hireben.spring.reactive.web.demo.storage.entity.File;
import reactor.core.publisher.Mono;

public interface FileService {
  Mono<File> headFile(String id, Precondition precondition);

  Mono<File> downloadFile(String id, Precondition precondition);

  Mono<File> uploadFile(File file, Precondition precondition);

  Mono<Void> removeFile(String id, String owner, Precondition precondition);
}
