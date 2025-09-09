package org.jdk.workmate.repository;

import static org.jooq.generated.tables.EmailCodes.EMAIL_CODES;

import java.time.Instant;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.jdk.workmate.util.TimeUtil;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class EmailCodeRepository {

  private final DSLContext dsl;

  public Optional<CodeEntry> findByEmail(String email) {
    var r = dsl.selectFrom(EMAIL_CODES).where(EMAIL_CODES.EMAIL.eq(email)).fetchOne();
    if (r == null) return Optional.empty();
    return Optional.of(
        new CodeEntry(
            r.getEmail(),
            r.getCode(),
            TimeUtil.toInstant(r.getExpireAt()),
            TimeUtil.toInstant(r.getLastSendAt()),
            TimeUtil.toInstant(r.getCreatedAt())));
  }

  public void upsertSend(String email, String code, Instant expireAt, Instant lastSendAt) {
    dsl.insertInto(EMAIL_CODES)
        .set(EMAIL_CODES.EMAIL, email)
        .set(EMAIL_CODES.CODE, code)
        .set(EMAIL_CODES.EXPIRE_AT, TimeUtil.toLdt(expireAt))
        .set(EMAIL_CODES.LAST_SEND_AT, TimeUtil.toLdt(lastSendAt))
        .onDuplicateKeyUpdate()
        .set(EMAIL_CODES.CODE, code)
        .set(EMAIL_CODES.EXPIRE_AT, TimeUtil.toLdt(expireAt))
        .set(EMAIL_CODES.LAST_SEND_AT, TimeUtil.toLdt(lastSendAt))
        .execute();
  }

  public void deleteByEmail(String email) {
    dsl.deleteFrom(EMAIL_CODES).where(EMAIL_CODES.EMAIL.eq(email)).execute();
  }

  public record CodeEntry(
      String email, String code, Instant expireAt, Instant lastSendAt, Instant createdAt) {}
}

