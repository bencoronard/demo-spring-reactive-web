package dev.hireben.spring.reactive.web.demo.storage.entity;

import java.time.Instant;

import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.MediaType;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import reactor.core.publisher.Flux;

@Data
@Builder(toBuilder = true)
public class File {

  private String id;
  private MediaType contentType;
  private Long contentLength;
  private String eTag;
  private Instant lastModified;
  private String owner;
  private Instant createdAt;

  @ToString.Exclude
  @EqualsAndHashCode.Exclude
  private Flux<DataBuffer> content;
}
