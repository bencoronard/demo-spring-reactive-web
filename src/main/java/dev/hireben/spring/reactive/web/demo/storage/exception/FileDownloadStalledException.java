package dev.hireben.spring.reactive.web.demo.storage.exception;

import dev.hireben.spring.reactive.web.demo.common.exception.api.ApplicationException;

public class FileDownloadStalledException extends ApplicationException {

  private static final String CODE = "DOWNLOAD_STALLED";

  public FileDownloadStalledException() {
    super(CODE);
  }

}
