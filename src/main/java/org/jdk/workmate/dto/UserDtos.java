package org.jdk.workmate.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class UserDtos {
  public record RegisterReq(
      @NotBlank String userName,
      @NotBlank String password,
      String displayName,
      @Email String email,
      @NotBlank String code) {}

  public record LoginReq(@NotBlank String userName, @NotBlank String password) {}

  public record EmailCodeReq(@Email String email, @NotBlank String purpose) {}

  public record EmailLoginReq(@Email String email, @NotBlank String code) {}

  public record MeResp(Long id, String userName, String displayName, String email) {}

  public record TokenResp(String token, MeResp user) {}
}
