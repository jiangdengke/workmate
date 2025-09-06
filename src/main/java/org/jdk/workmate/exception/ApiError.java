package org.jdk.workmate.exception;

import java.time.Instant;

/** 统一错误响应体 */
public record ApiError(int status, String error, String message, Instant timestamp) {}
