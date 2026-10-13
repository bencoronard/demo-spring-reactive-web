package dev.hireben.spring.reactive.web.demo.storage.entity;

import java.time.Instant;

import lombok.Builder;
import lombok.Data;

@Data
@Builder(toBuilder = true)
public class File {
  private String id;
  private String owner;
  private Instant createdAt;

  private String contentType;
  private long contentLength;

  private String eTag;
  private Instant lastModified;
  private String cacheControl;
}
