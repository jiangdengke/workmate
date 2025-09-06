package org.jdk.workmate.dto;

import jakarta.validation.constraints.NotBlank;
import java.time.Instant;

/** 笔记 DTO 定义 */
public class NoteDtos {
  public record CreateReq(@NotBlank String userName, @NotBlank String text) {}

  public record Resp(Long id, String userName, String text, Instant createdAt) {}
}
