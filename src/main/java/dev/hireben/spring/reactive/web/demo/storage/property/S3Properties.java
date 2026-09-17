package dev.hireben.spring.reactive.web.demo.storage.property;

import java.net.URI;
import java.time.Duration;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("aws.s3")
public record S3Properties(
    URI url,
    String bucket,
    HealthCheck healthCheck,
    Storage storage) {

  public record HealthCheck(
      Duration timeout,
      Duration cacheTtl) {
  }

  public record Storage(
      Key key) {
  }

  public record Key(
      String prefix,
      String metaOwner) {
  }

}
