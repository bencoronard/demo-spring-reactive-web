package dev.hireben.spring.reactive.web.demo.storage.exception;

import dev.hireben.spring.reactive.web.demo.storage.exception.api.StorageException;

public class FileDownloadStalledException extends StorageException {

  private static final String CODE = "DOWNLOAD_STALLED";

  public FileDownloadStalledException() {
    super(CODE);
  }

}
