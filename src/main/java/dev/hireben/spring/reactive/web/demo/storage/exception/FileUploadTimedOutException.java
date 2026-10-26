package dev.hireben.spring.reactive.web.demo.storage.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import dev.hireben.spring.reactive.web.demo.storage.exception.api.StorageException;

@ResponseStatus(HttpStatus.REQUEST_TIMEOUT)
public class FileUploadTimedOutException extends StorageException {

  private static final String CODE = "UPLOAD_TIMED_OUT";

  public FileUploadTimedOutException() {
    super(CODE);
  }

}
