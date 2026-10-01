package dev.hireben.spring.reactive.web.demo.storage.repository;

import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.stereotype.Repository;

import dev.hireben.spring.reactive.web.demo.common.utility.ExceptionUtil;
import dev.hireben.spring.reactive.web.demo.storage.entity.File;
import dev.hireben.spring.reactive.web.demo.storage.exception.FileNotFoundException;
import dev.hireben.spring.reactive.web.demo.storage.exception.FileUploadStalledException;
import dev.hireben.spring.reactive.web.demo.storage.exception.FileUploadTimedOutException;
import dev.hireben.spring.reactive.web.demo.storage.exception.FileWriteConflictException;
import dev.hireben.spring.reactive.web.demo.storage.property.S3Properties;
import dev.hireben.spring.reactive.web.demo.storage.repository.api.FileRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;
import software.amazon.awssdk.services.s3.model.S3Exception;

@Repository
@RequiredArgsConstructor
final class S3FileRepository implements FileRepository {

  private final S3Properties properties;
  private final S3AsyncClient client;

  private static <T> Mono<T> mapExceptions(Mono<T> mono) {
    return mono
        .onErrorMap(NoSuchKeyException.class, ex -> new FileNotFoundException())
        .onErrorMap(S3Exception.class, ex -> mapS3Exception(ex))
        .onErrorMap(SdkClientException.class, ex -> mapSdkClientException(ex));
  }

  private static Throwable mapS3Exception(S3Exception ex) {
    return switch (ex.statusCode()) {
      case 409 -> new FileWriteConflictException();
      default -> ex;
    };
  }

  private static Throwable mapSdkClientException(SdkClientException ex) {
    Throwable cause = ExceptionUtil.findCause(ex,
        FileUploadStalledException.class,
        FileUploadTimedOutException.class);
    return cause != null ? cause : ex;
  }

  @Override
  public Mono<File> head(File file) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'head'");
  }

  @Override
  public Flux<DataBuffer> load(File file) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'load'");
  }

  @Override
  public Mono<File> save(File file) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'save'");
  }

  @Override
  public Mono<File> update(File file) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'replace'");
  }

  @Override
  public Mono<Void> delete(File file) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'delete'");
  }

}
