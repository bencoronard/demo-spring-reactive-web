package dev.hireben.spring.reactive.web.demo.common.exception;

import java.time.Instant;

import org.springframework.http.CacheControl;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class NotModifiedException extends RuntimeException {

  private final String eTag;
  private final Instant lastModified;
  private final CacheControl cacheControl;

}
