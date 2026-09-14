package dev.hireben.spring.reactive.web.demo.common.configuration;

import java.util.Map;

import org.jspecify.annotations.Nullable;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.webflux.error.DefaultErrorAttributes;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;

@Component
class ErrorAttributes extends DefaultErrorAttributes {

  @Override
  public Map<String, @Nullable Object> getErrorAttributes(ServerRequest request, ErrorAttributeOptions options) {
    Map<String, Object> attributes = super.getErrorAttributes(request, options);
    attributes.put("code", "5000");
    return attributes;
  }

}
