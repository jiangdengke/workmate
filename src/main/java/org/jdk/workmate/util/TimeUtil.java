package org.jdk.workmate.util;

import java.time.*;

/** LocalDateTime 与 Instant 的固定 UTC 转换工具 */
public final class TimeUtil {
  private static final ZoneOffset Z = ZoneOffset.UTC;

  private TimeUtil() {}

  public static Instant toInstant(LocalDateTime ldt) {
    return ldt == null ? null : ldt.toInstant(Z);
  }

  public static LocalDateTime toLdt(Instant instant) {
    return instant == null ? null : LocalDateTime.ofInstant(instant, Z);
  }

  public static LocalDateTime nowLdtUtc() {
    return LocalDateTime.ofInstant(Instant.now(), Z);
  }
}
