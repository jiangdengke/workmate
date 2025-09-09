package org.jdk.workmate.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.Instant;

/** 提醒 DTO 定义 */
public class ReminderDtos {
  public record CreateReq(@NotBlank String text, @NotNull Instant atTime) {}

  public record Resp(
      Long id, String text, Instant atTime, boolean sent, Instant createdAt) {}
}
