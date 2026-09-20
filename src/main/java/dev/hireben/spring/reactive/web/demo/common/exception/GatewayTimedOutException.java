package dev.hireben.spring.reactive.web.demo.common.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import dev.hireben.spring.reactive.web.demo.common.exception.api.ApplicationException;

@ResponseStatus(HttpStatus.GATEWAY_TIMEOUT)
public class GatewayTimedOutException extends ApplicationException {

  private static final String CODE = "GATEWAY_TIMED_OUT";

  public GatewayTimedOutException() {
    super(CODE);
  }

}
