package dev.hireben.spring.reactive.web.demo.storage.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import dev.hireben.spring.reactive.web.demo.storage.exception.api.StorageException;

@ResponseStatus(HttpStatus.CONTENT_TOO_LARGE)
public class FileSizeTooLargeException extends StorageException {

  private static final String CODE = "CONTENT_TOO_LARGE";

  public FileSizeTooLargeException(long limit, long length) {
    super(CODE, new Object[] { length, limit });
  }

}
