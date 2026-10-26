package dev.hireben.spring.reactive.web.demo.common.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import dev.hireben.spring.reactive.web.demo.common.exception.api.ApplicationException;

@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class TokenExpiredException extends ApplicationException {

  private static final String CODE = "TOKEN_EXPIRED";

  public TokenExpiredException() {
    super(CODE);
  }

}
