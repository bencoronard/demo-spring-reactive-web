package dev.hireben.spring.reactive.web.demo.common.annotation;

import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.BindingContext;
import org.springframework.web.reactive.result.method.HandlerMethodArgumentResolver;
import org.springframework.web.server.ServerWebExchange;

import dev.hireben.spring.reactive.web.demo.common.annotation.api.AuthorizationHeader;
import dev.hireben.spring.reactive.web.demo.common.dto.Client;
import reactor.core.publisher.Mono;

@Component
final class AuthorizationHeaderResolver implements HandlerMethodArgumentResolver {

  @Override
  public boolean supportsParameter(MethodParameter parameter) {
    return parameter.hasParameterAnnotation(AuthorizationHeader.class)
        && parameter.getParameterType().equals(Client.class);
  }

  @Override
  public Mono<Object> resolveArgument(
      MethodParameter parameter,
      BindingContext bindingContext,
      ServerWebExchange exchange) {
    String header = exchange.getRequest().getHeaders().getFirst("Authorization");
    return Mono.just(Client.builder()
        .id(header)
        .build());
  }

}
