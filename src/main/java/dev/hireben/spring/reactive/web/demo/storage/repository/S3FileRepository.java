package dev.hireben.spring.reactive.web.demo.storage.repository;

import java.nio.ByteBuffer;
import java.time.Instant;
import java.util.Map;

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
import software.amazon.awssdk.core.async.AsyncRequestBody;
import software.amazon.awssdk.core.async.AsyncResponseTransformer;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.HeadObjectRequest;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
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
      case 409, 412 -> new FileWriteConflictException();
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
  public Mono<File> head(String id) {
    HeadObjectRequest request = HeadObjectRequest.builder()
        .bucket(properties.bucket())
        .key(String.join("/", properties.storage().key().prefix(), id))
        .build();
    Mono<File> mono = Mono.fromFuture(() -> client
        .headObject(request))
        .map(response -> File.builder()
            .id(id)
            .contentType(response.contentType())
            .contentLength(response.contentLength())
            .eTag(response.eTag())
            .lastModified(response.lastModified())
            .cacheControl(response.cacheControl())
            .owner(response.metadata().get(properties.storage().key().metaOwner()))
            .createdAt(Instant.parse(response.metadata().get(properties.storage().key().metaCreatedAt())))
            .build());
    return mapExceptions(mono);
  }

  @Override
  public Flux<ByteBuffer> load(String id, String eTag) {
    GetObjectRequest request = GetObjectRequest.builder()
        .bucket(properties.bucket())
        .key(String.join("/", properties.storage().key().prefix(), id))
        .ifMatch(eTag)
        .build();
    return Mono.fromFuture(() -> client
        .getObject(request, AsyncResponseTransformer.toPublisher()))
        .flatMapMany(publisher -> Flux.from(publisher));
  }

  @Override
  public Mono<File> save(File file, Flux<ByteBuffer> content) {
    PutObjectRequest.Builder builder = PutObjectRequest.builder()
        .bucket(properties.bucket())
        .key(String.join("/", properties.storage().key().prefix(), file.getId()))
        .metadata(Map.of(
            properties.storage().key().metaOwner(), file.getOwner(),
            properties.storage().key().metaCreatedAt(), file.getCreatedAt().toString()));
    if (file.getETag() != null) {
      builder.ifMatch(file.getETag());
    } else {
      builder.ifNoneMatch("*");
    }
    Mono<File> mono = Mono.fromFuture(() -> client
        .putObject(builder.build(), AsyncRequestBody.fromPublisher(content)))
        .map(response -> file.toBuilder()
            .eTag(response.eTag())
            .build());
    return mapExceptions(mono);
  }

  @Override
  public Mono<Void> delete(String id, String eTag) {
    DeleteObjectRequest request = DeleteObjectRequest.builder()
        .bucket(properties.bucket())
        .key(String.join("/", properties.storage().key().prefix(), id))
        .ifMatch(eTag)
        .build();
    Mono<Void> mono = Mono.fromFuture(() -> client
        .deleteObject(request))
        .then();
    return mapExceptions(mono);
  }

}
