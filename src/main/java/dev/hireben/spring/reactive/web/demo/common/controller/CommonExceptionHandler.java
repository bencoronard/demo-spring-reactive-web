package dev.hireben.spring.reactive.web.demo.common.controller;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.jspecify.annotations.Nullable;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.reactive.result.method.annotation.ResponseEntityExceptionHandler;
import org.springframework.web.server.ServerWebExchange;

import dev.hireben.spring.reactive.web.demo.common.exception.api.ApplicationException;
import dev.hireben.spring.reactive.web.demo.common.utility.ExceptionUtil;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.ElementKind;
import jakarta.validation.Path;
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

  // TODO: override handleMethodValidationException

  @ExceptionHandler(ConstraintViolationException.class)
  Mono<ResponseEntity<Object>> handleValidationException(
      ConstraintViolationException ex,
      ServerWebExchange exchange) {

    Collection<Map<String, String>> errors = ex.getConstraintViolations().stream()
        .map(violation -> Map.of(
            "field", clientFieldName(violation.getPropertyPath()),
            "message", violation.getMessage()))
        .toList();

    HttpStatus status = HttpStatus.BAD_REQUEST;

    ProblemDetail pd = ProblemDetail.forStatusAndDetail(
        status,
        getMessageSource().getMessage("common.error.validation_default",
            null,
            exchange.getLocaleContext().getLocale()));
    pd.setProperty("errors", errors);

    return super.createResponseEntity(pd, HttpHeaders.EMPTY, status, exchange);
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
        ExceptionUtil.errorMessageSourceKey(ex.getMessageCodePrefix(), ex.getCode().toLowerCase()),
        ex.getArguments(),
        exchange);

    return super.createResponseEntity(pd, HttpHeaders.EMPTY, status, exchange);
  }

  private static String clientFieldName(Path path) {
    List<String> names = new ArrayList<>();
    for (Path.Node node : path) {
      ElementKind kind = node.getKind();
      if (kind == ElementKind.PARAMETER || kind == ElementKind.PROPERTY) {
        names.add(node.getName());
      }
    }
    return String.join(".", names);
  }

}
