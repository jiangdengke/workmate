package org.jdk.workmate.service;

import java.util.Collection;
import lombok.RequiredArgsConstructor;
import org.jdk.workmate.dto.UserDtos;
import org.jdk.workmate.repository.UserRepository;
import org.jdk.workmate.security.JwtService;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

  private final UserRepository repo;
  private final PasswordEncoder encoder;
  private final JwtService jwtService;
  private final EmailCodeService emailCodeService;

  @Transactional
  public UserDtos.TokenResp register(UserDtos.RegisterReq req) {
    repo
        .findByUserName(req.userName())
        .ifPresent(
            it -> {
              throw new IllegalArgumentException("用户名已存在");
            });
    if (req.email() == null || req.email().isBlank()) {
      throw new IllegalArgumentException("邮箱必填");
    }
    // 验证邮箱验证码（注册）
    emailCodeService.validateAndConsume(req.email(), "register", req.code());
    String hash = encoder.encode(req.password());
    Long id = repo.create(req.userName(), hash, req.displayName(), req.email());
    var me = new UserDtos.MeResp(id, req.userName(), req.displayName(), req.email());
    String token = jwtService.issueToken(id, req.userName());
    return new UserDtos.TokenResp(token, me);
  }

  @Transactional(readOnly = true)
  public UserDtos.TokenResp login(UserDtos.LoginReq req) {
    var user =
        repo
            .findByUserName(req.userName())
            .orElseThrow(() -> new UsernameNotFoundException("用户不存在"));
    if (!encoder.matches(req.password(), user.password())) {
      throw new UsernameNotFoundException("用户名或密码错误");
    }
    var me = new UserDtos.MeResp(user.id(), user.userName(), user.displayName(), user.email());
    String token = jwtService.issueToken(user.id(), user.userName());
    return new UserDtos.TokenResp(token, me);
  }

  @Transactional(readOnly = true)
  public UserDtos.TokenResp loginByEmail(String email, String code) {
    // 验证邮箱验证码（登录）
    emailCodeService.validateAndConsume(email, "login", code);
    var user =
        repo
            .findByEmail(email)
            .orElseThrow(() -> new UsernameNotFoundException("邮箱未注册"));
    var me = new UserDtos.MeResp(user.id(), user.userName(), user.displayName(), user.email());
    String token = jwtService.issueToken(user.id(), user.userName());
    return new UserDtos.TokenResp(token, me);
  }

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    var u =
        repo
            .findByUserName(username)
            .orElseThrow(() -> new UsernameNotFoundException("用户不存在"));
    Collection<? extends GrantedAuthority> auths =
        java.util.List.of(new SimpleGrantedAuthority("ROLE_USER"));
    return new User(u.userName(), u.password(), auths);
  }
}
