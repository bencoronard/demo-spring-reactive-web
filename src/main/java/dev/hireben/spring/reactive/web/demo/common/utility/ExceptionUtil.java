package dev.hireben.spring.reactive.web.demo.common.utility;

import lombok.experimental.UtilityClass;

@UtilityClass
public class ExceptionUtil {

  @SafeVarargs
  public Throwable findCause(Throwable ex, Class<? extends Throwable>... types) {
    Throwable current = ex;
    while (current != null) {
      for (Class<? extends Throwable> type : types) {
        if (type.isInstance(current)) {
          return current;
        }
      }
      current = current.getCause();
    }
    return null;
  }

}
