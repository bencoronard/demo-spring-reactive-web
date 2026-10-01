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
      Head head,
      Delete delete,
      Download download,
      Upload upload) {

    public record Head(
        Duration timeout) {
    }

    public record Delete(
        Duration timeout) {
    }

    public record Download(
        Duration timeout,
        Duration idleTimeout) {
    }

    public record Upload(
        Duration timeout,
        Duration idleTimeout,
        Integer idMaxLength,
        Long contentMaxLength,
        Set<MediaType> contentTypesAllowed,
        Set<String> contentGroupsAllowed) {
    }

  }

}
