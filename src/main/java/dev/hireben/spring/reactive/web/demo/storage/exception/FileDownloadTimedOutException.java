package dev.hireben.spring.reactive.web.demo.storage.exception;

import dev.hireben.spring.reactive.web.demo.common.exception.api.ApplicationException;

public class FileDownloadTimedOutException extends ApplicationException {

  private static final String CODE = "DOWNLOAD_TIMED_OUT";

  public FileDownloadTimedOutException() {
    super(CODE);
  }

}
