package org.jdk.workmate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.jdk.workmate.dto.UserDtos;
import org.jdk.workmate.service.UserService;
import org.jdk.workmate.service.EmailCodeService;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

  private final UserService userService;
  private final EmailCodeService emailCodeService;

  @PostMapping("/register")
  @ResponseStatus(HttpStatus.CREATED)
  public UserDtos.TokenResp register(@RequestBody @Valid UserDtos.RegisterReq req) {
    return userService.register(req);
  }

  @PostMapping("/login")
  public UserDtos.TokenResp login(@RequestBody @Valid UserDtos.LoginReq req) {
    return userService.login(req);
  }

  @PostMapping("/email/code")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void sendEmailCode(@RequestBody @Valid UserDtos.EmailCodeReq req) {
    emailCodeService.sendCode(req.email(), req.purpose());
  }

  @PostMapping("/login-email")
  public UserDtos.TokenResp loginByEmail(@RequestBody @Valid UserDtos.EmailLoginReq req) {
    return userService.loginByEmail(req.email(), req.code());
  }
}
