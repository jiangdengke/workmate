package org.jdk.workmate.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtService {

  private final Algorithm algorithm;
  private final long ttlSeconds;

  public JwtService(
      @Value("${app.jwt.secret:change-me}") String secret,
      @Value("${app.jwt.ttlSeconds:2592000}") long ttlSeconds) {
    this.algorithm = Algorithm.HMAC256(secret);
    this.ttlSeconds = ttlSeconds;
  }

  public String issueToken(long userId, String userName) {
    Instant now = Instant.now();
    return JWT.create()
        .withSubject(String.valueOf(userId))
        .withClaim("un", userName)
        .withIssuedAt(now)
        .withExpiresAt(now.plus(ttlSeconds, ChronoUnit.SECONDS))
        .sign(algorithm);
  }

  public DecodedJWT verify(String token) {
    return JWT.require(algorithm).build().verify(token);
  }
}

