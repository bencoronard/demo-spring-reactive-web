package dev.hireben.spring.reactive.web.demo.common.property;

import java.util.Set;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("audit")
public record AuditProperties(
    AccessLog accessLog) {

  public record AccessLog(
      Set<String> exclude) {
  }

}
