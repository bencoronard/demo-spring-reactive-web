package dev.hireben.spring.reactive.web.demo.common.annotation;

import org.springframework.core.MethodParameter;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.BindingContext;
import org.springframework.web.reactive.result.method.HandlerMethodArgumentResolver;
import org.springframework.web.server.ServerWebExchange;

import dev.hireben.spring.reactive.web.demo.common.annotation.api.AuthorizationHeader;
import dev.hireben.spring.reactive.web.demo.common.dto.Client;
import dev.hireben.spring.reactive.web.demo.common.exception.UnauthenticatedClientException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtParser;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
final class AuthorizationHeaderResolver implements HandlerMethodArgumentResolver {

  private final JwtParser parser;

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

    String header = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

    if (header == null || header.isBlank()) {
      throw new UnauthenticatedClientException();
    }

    Claims claims;
    try {
      claims = parser.parseSignedClaims(header.substring("Bearer ".length())).getPayload();
    } catch (Exception e) {
      throw new UnauthenticatedClientException();
    }

    if (claims == null || claims.getSubject() == null || claims.getSubject().isBlank()) {
      throw new UnauthenticatedClientException();
    }

    return Mono.just(Client.builder()
        .id(claims.getSubject())
        .build());
  }

}
