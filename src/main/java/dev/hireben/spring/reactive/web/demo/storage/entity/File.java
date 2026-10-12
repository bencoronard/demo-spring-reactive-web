package dev.hireben.spring.reactive.web.demo.storage.entity;

import java.time.Instant;

import org.springframework.http.CacheControl;
import org.springframework.http.MediaType;

import lombok.Builder;
import lombok.Data;

@Data
@Builder(toBuilder = true)
public class File {
  private String id;
  private String owner;
  private Instant createdAt;

  private MediaType contentType;
  private long contentLength;

  private String eTag;
  private Instant lastModified;
  private CacheControl cacheControl;
}
