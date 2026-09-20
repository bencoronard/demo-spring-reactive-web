package dev.hireben.spring.reactive.web.demo.common.dto;

import java.time.Instant;
import java.util.List;

public record Precondition(
    List<String> ifMatch,
    List<String> ifNoneMatch,
    Instant ifModifiedSince,
    Instant ifUnmodifiedSince) {
}
