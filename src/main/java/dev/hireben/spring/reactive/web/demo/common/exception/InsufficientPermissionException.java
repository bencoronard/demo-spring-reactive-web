package dev.hireben.spring.reactive.web.demo.common.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import dev.hireben.spring.reactive.web.demo.common.exception.api.ApplicationException;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class InsufficientPermissionException extends ApplicationException {

  private static final String CODE = "UNAUTHORIZED";

  public InsufficientPermissionException() {
    super(CODE);
  }

}
