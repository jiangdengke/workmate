package org.jdk.workmate.dto;

import jakarta.validation.constraints.NotBlank;
import java.time.Instant;

/** 待办 DTO 定义 */
public class TodoDtos {
  /** 新建请求 */
  public record CreateReq(@NotBlank String title) {}

  /** 修改完成状态请求 */
  public record UpdateDoneReq(boolean done) {}

  /** 响应体 */
  public record Resp(Long id, String title, boolean done, Instant createdAt, Instant doneAt) {}
}
