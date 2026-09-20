package dev.hireben.spring.reactive.web.demo.storage.property;

import java.net.URI;
import java.time.Duration;
import java.util.Set;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.http.MediaType;

@ConfigurationProperties("storage")
public record StorageProperties(
    URI url,
    File file) {

  public record File(
      Supported supported,
      Operation operation) {

    public record Supported(
        Id id,
        Content content) {

      public record Id(
          Integer length) {
      }

      public record Content(
          Long length,
          Set<String> groups,
          Set<MediaType> types) {
      }
    }

    public record Operation(
        Duration timeout,
        Upload upload,
        Download download) {

      public record Upload(
          Duration timeout,
          Duration idleTimeout) {
      }

      public record Download(
          Duration timeout,
          Duration idleTimeout) {
      }
    }
  }

}
