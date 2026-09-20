package dev.hireben.spring.reactive.web.demo.common.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import dev.hireben.spring.reactive.web.demo.common.exception.api.ApplicationException;

@ResponseStatus(HttpStatus.PRECONDITION_FAILED)
public class PreconditionFailedException extends ApplicationException {

  private static final String CODE = "PRECONDITION_FAILED";

  public PreconditionFailedException() {
    super(CODE);
  }

}
