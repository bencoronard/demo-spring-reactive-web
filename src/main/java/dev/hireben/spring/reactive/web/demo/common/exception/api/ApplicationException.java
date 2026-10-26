package dev.hireben.spring.reactive.web.demo.common.exception.api;

import lombok.Getter;

@Getter
public abstract class ApplicationException extends RuntimeException {

  private final String code;
  private final Object[] arguments;

  protected ApplicationException(String code) {
    this.code = code;
    this.arguments = null;
  }

  protected ApplicationException(String code, Object[] arguments) {
    this.code = code;
    this.arguments = arguments;
  }

  public String getMessageCodePrefix() {
    return "common";
  }

}
