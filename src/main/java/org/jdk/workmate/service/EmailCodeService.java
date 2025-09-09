package org.jdk.workmate.service;

import java.time.Duration;
import java.time.Instant;
import java.util.Random;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.jdk.workmate.repository.EmailCodeRepository;

@Service
@RequiredArgsConstructor
public class EmailCodeService {

  private static final Duration EXPIRE = Duration.ofMinutes(10);
  private static final Duration COOLDOWN = Duration.ofSeconds(60);

  private final JavaMailSender mailSender;
  private final EmailCodeRepository repo;

  @Value("${spring.mail.username:}")
  private String mailFrom;

  private final Random random = new Random();
  

  public void sendCode(String email, String purpose) {
    Instant now = Instant.now();
    var cur = repo.findByEmail(email).orElse(null);
    if (cur != null && cur.lastSendAt().isAfter(now.minus(COOLDOWN))) {
      throw new IllegalStateException("发送过于频繁，请稍后再试");
    }
    String code = String.format("%06d", random.nextInt(1_000_000));
    repo.upsertSend(email, code, now.plus(EXPIRE), now);

    SimpleMailMessage msg = new SimpleMailMessage();
    if (mailFrom != null && !mailFrom.isBlank()) {
      msg.setFrom(mailFrom);
    }
    msg.setTo(email);
    msg.setSubject("Workmate 验证码");
    msg.setText("您的验证码是：" + code + "，" + (EXPIRE.toMinutes()) + "分钟内有效。用途：" + purpose);
    mailSender.send(msg);
  }

  public void validateAndConsume(String email, String purpose, String code) {
    var cur = repo.findByEmail(email).orElse(null);
    if (cur == null) throw new IllegalArgumentException("验证码无效");
    if (Instant.now().isAfter(cur.expireAt())) {
      repo.deleteByEmail(email);
      throw new IllegalArgumentException("验证码已过期");
    }
    if (!cur.code().equals(code)) throw new IllegalArgumentException("验证码错误");
    repo.deleteByEmail(email);
  }
}
