package dev.hireben.spring.reactive.web.demo.storage.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import dev.hireben.spring.reactive.web.demo.storage.exception.api.StorageException;

@ResponseStatus(HttpStatus.CONFLICT)
public class FileWriteConflictException extends StorageException {

  private static final String CODE = "WRITE_CONFLICTED";

  public FileWriteConflictException(String id) {
    super(CODE, new Object[] { id });
  }

}
