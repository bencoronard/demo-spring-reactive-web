package dev.hireben.spring.reactive.web.demo.storage.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.ResponseStatus;

import dev.hireben.spring.reactive.web.demo.storage.exception.api.StorageException;

@ResponseStatus(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
public class FileTypeNotSupportedException extends StorageException {

  private static final String CODE = "UNSUPPORTED_MEDIA_TYPE";

  public FileTypeNotSupportedException(MediaType contentType) {
    super(CODE, new Object[] { contentType });
  }

}
