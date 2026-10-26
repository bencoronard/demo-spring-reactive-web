package dev.hireben.spring.reactive.web.demo.common.exception;

import java.util.Collection;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import dev.hireben.spring.reactive.web.demo.common.exception.api.ApplicationException;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class MissingRequiredHeadersException extends ApplicationException {

  private static final String CODE = "MISSING_HEADERS";

  public MissingRequiredHeadersException(Collection<String> headers) {
    super(CODE, new Object[] { String.join(", ", headers) });
  }

}
