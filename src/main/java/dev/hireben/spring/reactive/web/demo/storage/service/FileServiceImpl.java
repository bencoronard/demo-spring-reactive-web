package dev.hireben.spring.reactive.web.demo.storage.service;

import java.io.FileNotFoundException;
import org.springframework.stereotype.Service;

import dev.hireben.spring.reactive.web.demo.common.exception.GatewayTimedOutException;
import dev.hireben.spring.reactive.web.demo.common.exception.NotModifiedException;
import dev.hireben.spring.reactive.web.demo.common.exception.OperationDeniedException;
import dev.hireben.spring.reactive.web.demo.common.exception.PreconditionFailedException;
import dev.hireben.spring.reactive.web.demo.storage.entity.File;
import dev.hireben.spring.reactive.web.demo.storage.exception.FileDownloadStalledException;
import dev.hireben.spring.reactive.web.demo.storage.exception.FileDownloadTimedOutException;
import dev.hireben.spring.reactive.web.demo.storage.exception.FileUploadStalledException;
import dev.hireben.spring.reactive.web.demo.storage.exception.FileUploadTimedOutException;
import dev.hireben.spring.reactive.web.demo.storage.property.StorageProperties;
import dev.hireben.spring.reactive.web.demo.storage.repository.api.FileRepository;
import dev.hireben.spring.reactive.web.demo.storage.service.api.FileService;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
final class FileServiceImpl implements FileService {

  private final StorageProperties properties;
  private final FileRepository repository;

  @Override
  public Mono<File> info(File file) {
    return repository
        .head(file)
        .timeout(
            properties.file().operation().timeout(),
            Mono.error(GatewayTimedOutException::new))
        .switchIfEmpty(Mono.error(FileNotFoundException::new))
        .handle((existing, sink) -> {
          if ((file.getETag() != null && file.getETag().equals(existing.getETag())) ||
              (file.getLastModified() != null && file.getLastModified().isAfter(existing.getLastModified()))) {
            sink.error(
                new NotModifiedException(existing.getETag(), existing.getLastModified(), existing.getCacheControl()));
            return;
          }
          sink.next(existing);
        });
  }

  @Override
  public Mono<File> download(File file) {
    return this
        .info(file)
        .map(existing -> existing.toBuilder()
            .content(repository
                .load(existing)
                .timeout(
                    properties.file().operation().download().idleTimeout(),
                    Mono.error(FileDownloadStalledException::new))
                .takeUntilOther(
                    Mono.delay(properties.file().operation().download().timeout())
                        .flatMap(tick -> Mono.error(FileDownloadTimedOutException::new))))
            .build());
  }

  @Override
  public Mono<File> upload(File file) {
    return repository
        .head(file)
        .timeout(
            properties.file().operation().timeout(),
            Mono.error(GatewayTimedOutException::new))
        .filter(existing -> file.getETag() == null || file.getETag().equals(existing.getETag()))
        .switchIfEmpty(Mono.defer(() -> repository.save(file)));
  }

  @Override
  public Mono<File> replace(File file) {
    return repository
        .head(file)
        .timeout(
            properties.file().operation().timeout(),
            Mono.error(GatewayTimedOutException::new))
        .switchIfEmpty(Mono.error(FileNotFoundException::new))
        // Update
        .filter(existing -> existing.getOwner().equals(file.getOwner()))
        .switchIfEmpty(Mono.error(OperationDeniedException::new))
        .filter(existing -> file.getETag() == null || file.getETag().equals(existing.getETag()))
        .switchIfEmpty(Mono.error(PreconditionFailedException::new))
        .flatMap(existing -> repository.update(file.toBuilder()
            .eTag(existing.getETag())
            .content(file.getContent()
                .timeout(
                    properties.file().operation().upload().idleTimeout(),
                    Mono.error(FileUploadStalledException::new))
                .takeUntilOther(
                    Mono.delay(properties.file().operation().upload().timeout())
                        .flatMap(tick -> Mono.error(FileUploadTimedOutException::new))))
            .build()))
        // Insert
        .onErrorResume(FileNotFoundException.class, ex -> {
          if (file.getETag() != null) {
            return Mono.error(ex);
          }
          return repository.save(file.toBuilder()
              .content(file.getContent()
                  .timeout(
                      properties.file().operation().upload().idleTimeout(),
                      Mono.error(FileUploadStalledException::new))
                  .takeUntilOther(
                      Mono.delay(properties.file().operation().upload().timeout())
                          .flatMap(tick -> Mono.error(FileUploadTimedOutException::new))))
              .build());
        });
  }

  @Override
  public Mono<Void> remove(File file) {
    return repository
        .head(file)
        .timeout(
            properties.file().operation().timeout(),
            Mono.error(GatewayTimedOutException::new))
        .switchIfEmpty(Mono.error(FileNotFoundException::new))
        .filter(existing -> existing.getOwner().equals(file.getOwner()))
        .switchIfEmpty(Mono.error(OperationDeniedException::new))
        .filter(existing -> file.getETag() == null || file.getETag().equals(existing.getETag()))
        .switchIfEmpty(Mono.error(PreconditionFailedException::new))
        .flatMap(existing -> repository
            .delete(existing)
            .timeout(
                properties.file().operation().timeout(),
                Mono.error(GatewayTimedOutException::new)))
        .onErrorComplete(FileNotFoundException.class);
  }

}
