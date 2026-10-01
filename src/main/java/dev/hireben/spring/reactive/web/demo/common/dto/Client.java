package dev.hireben.spring.reactive.web.demo.common.dto;

import lombok.Builder;

@Builder
public record Client(
    String id) {
}
