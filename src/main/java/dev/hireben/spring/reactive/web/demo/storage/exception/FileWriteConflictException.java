package dev.hireben.spring.reactive.web.demo.storage.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import dev.hireben.spring.reactive.web.demo.common.exception.api.ApplicationException;

@ResponseStatus(HttpStatus.CONFLICT)
public class FileWriteConflictException extends ApplicationException {

  private static final String CODE = "WRITE_CONFLICTED";

  public FileWriteConflictException() {
    super(CODE);
  }

}
