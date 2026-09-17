package dev.hireben.spring.reactive.web.demo.common.controller;

import org.jspecify.annotations.Nullable;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.reactive.result.method.annotation.ResponseEntityExceptionHandler;
import org.springframework.web.server.ServerWebExchange;

@RestControllerAdvice
final class CommonExceptionHandler extends ResponseEntityExceptionHandler {

  @Override
  protected ProblemDetail createProblemDetail(
      Exception ex,
      HttpStatusCode status,
      String defaultDetail,
      @Nullable String detailMessageCode,
      Object @Nullable [] detailMessageArguments,
      ServerWebExchange exchange) {

    ProblemDetail pd = super.createProblemDetail(
        ex,
        status,
        defaultDetail,
        detailMessageCode,
        detailMessageArguments,
        exchange);

    pd.setProperty("code", "4000");

    return pd;
  }

}
