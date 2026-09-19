package dev.hireben.spring.reactive.web.demo.common.configuration;

import java.util.Map;

import org.jspecify.annotations.Nullable;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.webflux.error.DefaultErrorAttributes;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;

import lombok.RequiredArgsConstructor;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

@Component
@RequiredArgsConstructor
final class ErrorAttributes extends DefaultErrorAttributes {

  private final ObjectMapper objectMapper;
  private final MessageSource messageSource;

  @Override
  public Map<String, @Nullable Object> getErrorAttributes(ServerRequest request, ErrorAttributeOptions options) {

    String code = "UNCAUGHT";

    ProblemDetail pd = ProblemDetail.forStatusAndDetail(
        HttpStatus.INTERNAL_SERVER_ERROR,
        messageSource.getMessage(
            String.join(".", "common", "error", code.toLowerCase()),
            null,
            request.exchange().getLocaleContext().getLocale()));

    pd.setProperty("code", code);
    pd.setProperty("id", request.exchange().getRequest().getId());

    return objectMapper.convertValue(pd, new TypeReference<Map<String, Object>>() {
    });
  }

}
