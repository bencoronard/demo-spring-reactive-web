package dev.hireben.spring.reactive.web.demo.storage.service;

import java.time.Instant;

import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.stereotype.Service;

import dev.hireben.spring.reactive.web.demo.common.exception.GatewayTimedOutException;
import dev.hireben.spring.reactive.web.demo.storage.entity.File;
import dev.hireben.spring.reactive.web.demo.storage.exception.FileDownloadStalledException;
import dev.hireben.spring.reactive.web.demo.storage.exception.FileDownloadTimedOutException;
import dev.hireben.spring.reactive.web.demo.storage.exception.FileNotFoundException;
import dev.hireben.spring.reactive.web.demo.storage.property.StorageProperties;
import dev.hireben.spring.reactive.web.demo.storage.repository.api.FileRepository;
import dev.hireben.spring.reactive.web.demo.storage.service.api.FileService;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
final class FileServiceImpl implements FileService {

  private final StorageProperties properties;
  private final FileRepository repository;

  @Override
  public Mono<File> info(String id) {
    return repository
        .head(id)
        .timeout(
            properties.file().operation().timeout(),
            Mono.error(GatewayTimedOutException::new))
        .switchIfEmpty(Mono.error(FileNotFoundException::new));
  }

  @Override
  public Flux<DataBuffer> download(String id, String eTag) {
    return repository
        .load(id, eTag)
        .timeout(
            properties.file().operation().download().idleTimeout(),
            Mono.error(FileDownloadStalledException::new))
        .takeUntilOther(
            Mono.delay(properties.file().operation().download().timeout())
                .flatMap(tick -> Mono.error(FileDownloadTimedOutException::new)));
  }

  @Override
  public Mono<Void> remove(String id, String eTag) {
    return repository
        .delete(id, eTag)
        .timeout(
            properties.file().operation().timeout(),
            Mono.error(GatewayTimedOutException::new));
  }

  @Override
  public Mono<File> upload(File file, Flux<DataBuffer> content) {
    return repository
        .save(
            file.toBuilder()
                .createdAt(Instant.now())
                .lastModified(Instant.now())
                .build(),
            content);
  }

  @Override
  public Mono<File> replace(File file, Flux<DataBuffer> content) {
    return repository
        .update(
            file.toBuilder()
                .build(),
            content);
  }

}
