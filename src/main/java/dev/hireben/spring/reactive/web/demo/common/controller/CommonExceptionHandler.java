package dev.hireben.spring.reactive.web.demo.common.controller;

import org.jspecify.annotations.Nullable;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.http.ResponseEntity.BodyBuilder;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.reactive.result.method.annotation.ResponseEntityExceptionHandler;
import org.springframework.web.server.ServerWebExchange;

import dev.hireben.spring.reactive.web.demo.common.exception.NotModifiedException;
import dev.hireben.spring.reactive.web.demo.common.exception.api.ApplicationException;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RestControllerAdvice
@RequiredArgsConstructor
final class CommonExceptionHandler extends ResponseEntityExceptionHandler {

  @Override
  protected ProblemDetail createProblemDetail(
      Exception ex,
      HttpStatusCode status,
      String defaultDetail,
      @Nullable String detailMessageCode,
      Object @Nullable [] detailMessageArguments,
      ServerWebExchange exchange) {

    String code = "UNHANDLED";

    if (ex instanceof ApplicationException appEx) {
      code = appEx.getCode();
    }

    ProblemDetail pd = super.createProblemDetail(
        ex,
        status,
        defaultDetail,
        detailMessageCode,
        detailMessageArguments,
        exchange);

    pd.setProperty("code", code);
    pd.setProperty("id", exchange.getRequest().getId());

    return pd;
  }

  @ExceptionHandler(ApplicationException.class)
  Mono<ResponseEntity<Object>> handleApplicationException(
      ApplicationException ex,
      ServerWebExchange exchange) {

    if (exchange.getResponse().isCommitted()) {
      return Mono.error(ex);
    }

    ResponseStatus responseStatus = AnnotatedElementUtils.findMergedAnnotation(ex.getClass(), ResponseStatus.class);

    HttpStatus status = responseStatus != null ? responseStatus.value() : HttpStatus.INTERNAL_SERVER_ERROR;

    ProblemDetail pd = createProblemDetail(
        ex,
        status,
        null,
        String.join(".", "common", "error", ex.getCode().toLowerCase()),
        null,
        exchange);

    return super.createResponseEntity(pd, HttpHeaders.EMPTY, status, exchange);
  }

  @ExceptionHandler(NotModifiedException.class)
  Mono<ResponseEntity<Void>> handleNotModifiedException(NotModifiedException ex) {

    BodyBuilder response = ResponseEntity.status(HttpStatus.NOT_MODIFIED);

    if (ex.getETag() != null && !ex.getETag().isBlank()) {
      response.eTag(ex.getETag());
    }

    if (ex.getLastModified() != null) {
      response.lastModified(ex.getLastModified());
    }

    return Mono.just(response.build());
  }

}
