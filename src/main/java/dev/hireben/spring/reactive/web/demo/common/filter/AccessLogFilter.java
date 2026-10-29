package dev.hireben.spring.reactive.web.demo.common.filter;

import java.util.Collection;
import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;

import dev.hireben.spring.reactive.web.demo.common.property.AuditProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
final class AccessLogFilter implements WebFilter {

  private final AuditProperties properties;

  @Override
  public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
    String path = exchange.getRequest().getPath().pathWithinApplication().value();
    if (excluded(path)) {
      return chain.filter(exchange);
    }

    long start = System.currentTimeMillis();

    log.info("{}HTTP {} {}",
        exchange.getLogPrefix(),
        exchange.getRequest().getMethod(),
        path);

    return chain.filter(exchange)
        .doFinally(signalType -> log.info("{}HTTP {} {}ms",
            exchange.getLogPrefix(),
            exchange.getResponse().getStatusCode(),
            System.currentTimeMillis() - start));
  }

  private boolean excluded(String path) {
    for (String prefix : properties.accessLog().exclude()) {
      if (path.startsWith(prefix)) {
        return true;
      }
    }
    return false;
  }
}
