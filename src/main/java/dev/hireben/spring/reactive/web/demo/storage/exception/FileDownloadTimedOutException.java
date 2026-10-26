package dev.hireben.spring.reactive.web.demo.storage.exception;

import dev.hireben.spring.reactive.web.demo.storage.exception.api.StorageException;

public class FileDownloadTimedOutException extends StorageException {

  private static final String CODE = "DOWNLOAD_TIMED_OUT";

  public FileDownloadTimedOutException() {
    super(CODE);
  }

}
