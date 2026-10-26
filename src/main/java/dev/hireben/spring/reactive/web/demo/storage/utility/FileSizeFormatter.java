package dev.hireben.spring.reactive.web.demo.storage.utility;

import lombok.experimental.UtilityClass;

@UtilityClass
public class FileSizeFormatter {

  private final String[] UNITS = { "B", "KB", "MB", "GB", "TB" };

  public String format(long bytes) {
    if (bytes < 0) {
      throw new IllegalArgumentException("Received negative byte count");
    }

    if (bytes < 1024) {
      return bytes + " B";
    }

    int unitIndex = (int) (Math.log(bytes) / Math.log(1024));

    unitIndex = Math.min(unitIndex, UNITS.length - 1);

    double value = bytes / Math.pow(1024, unitIndex);

    return "%.2f %s".formatted(value, UNITS[unitIndex]);
  }

}
