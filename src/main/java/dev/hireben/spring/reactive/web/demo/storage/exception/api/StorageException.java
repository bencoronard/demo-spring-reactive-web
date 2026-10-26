package dev.hireben.spring.reactive.web.demo.storage.exception.api;

import dev.hireben.spring.reactive.web.demo.common.exception.api.ApplicationException;

public abstract class StorageException extends ApplicationException {

  protected StorageException(String code) {
    super(code);
  }

  protected StorageException(String code, Object[] arguments) {
    super(code, arguments);
  }

  @Override
  public String getMessageCodePrefix() {
    return "storage";
  }

}
