package dev.hireben.spring.reactive.web.demo.storage.healthcheck;

import java.time.Instant;
import java.util.concurrent.atomic.AtomicReference;

import org.springframework.boot.health.contributor.Health;
import org.springframework.boot.health.contributor.ReactiveHealthIndicator;
import org.springframework.stereotype.Component;

import dev.hireben.spring.reactive.web.demo.storage.property.S3Properties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.services.s3.model.HeadBucketRequest;

@Slf4j
@Component("s3Storage")
@RequiredArgsConstructor
final class S3StorageHealthIndicator implements ReactiveHealthIndicator {

  private final S3Properties properties;
  private final S3AsyncClient client;
  private final AtomicReference<CachedResult> cache = new AtomicReference<>();

  private record CachedResult(Health health, Instant expiresAt) {
  }

  @Override
  public Mono<Health> health() {

    CachedResult current = cache.get();
    if (current != null && Instant.now().isBefore(current.expiresAt)) {
      return Mono.just(current.health());
    }

    HeadBucketRequest request = HeadBucketRequest.builder()
        .bucket(properties.bucket())
        .build();

    return Mono.fromFuture(() -> client.headBucket(request))
        .timeout(properties.healthCheck().timeout())
        .map(response -> Health.up().build())
        .onErrorResume(ex -> {
          log.warn("S3 health check failed.", ex);
          return Mono.just(Health.down().build());
        })
        .doOnNext(
            health -> cache.set(new CachedResult(health, Instant.now().plus(properties.healthCheck().cacheTtl()))));
  }

}
