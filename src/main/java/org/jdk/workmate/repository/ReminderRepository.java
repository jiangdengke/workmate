package org.jdk.workmate.repository;

import static org.jooq.generated.tables.Reminders.REMINDERS;

import java.time.Instant;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.jdk.workmate.dto.ReminderDtos;
import org.jdk.workmate.exception.NotFoundException;
import org.jdk.workmate.util.TimeUtil;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

/** 提醒数据访问层 */
@Repository
@RequiredArgsConstructor
public class ReminderRepository {

  private final DSLContext dsl;

  public Long create(long userId, String text, Instant at) {
    return dsl.insertInto(REMINDERS)
        .set(REMINDERS.USER_ID, userId)
        .set(REMINDERS.TEXT, text)
        .set(REMINDERS.AT_TIME, TimeUtil.toLdt(at))
        .returningResult(REMINDERS.ID)
        .fetchOne(REMINDERS.ID, Long.class);
  }

  public ReminderDtos.Resp get(long id) {
    var r = dsl.selectFrom(REMINDERS).where(REMINDERS.ID.eq(id)).fetchOne();
    if (r == null) throw new NotFoundException("Reminder not found: " + id);
    return map(r.getId(), r.getText(), r.getAtTime(), r.getSent(), r.getCreatedAt());
  }

  public int delete(long userId, long id) {
    return dsl.deleteFrom(REMINDERS)
        .where(REMINDERS.ID.eq(id).and(REMINDERS.USER_ID.eq(userId)))
        .execute();
  }

  public List<ReminderDtos.Resp> listByUser(
      long userId, String status, int page, int size, Instant now) {
    var sel = dsl.selectFrom(REMINDERS).where(REMINDERS.USER_ID.eq(userId));
    var nowLdt = TimeUtil.toLdt(now);

    switch (status == null ? "all" : status.toLowerCase()) {
      case "upcoming" -> sel.and(REMINDERS.SENT.eq(false)).and(REMINDERS.AT_TIME.gt(nowLdt));
      case "due" -> sel.and(REMINDERS.SENT.eq(false)).and(REMINDERS.AT_TIME.le(nowLdt));
      case "sent" -> sel.and(REMINDERS.SENT.eq(true));
      default -> {}
    }

    return sel.orderBy(REMINDERS.AT_TIME.asc())
        .limit(size)
        .offset(page * size)
        .fetch(
            r ->
                map(
                    r.getId(), r.getText(), r.getAtTime(), r.getSent(), r.getCreatedAt()));
  }

  public List<ReminderDtos.Resp> listDue(int max, Instant now) {
    var nowLdt = TimeUtil.toLdt(now);
    return dsl.selectFrom(REMINDERS)
        .where(REMINDERS.SENT.eq(false).and(REMINDERS.AT_TIME.le(nowLdt)))
        .orderBy(REMINDERS.AT_TIME.asc())
        .limit(max)
        .fetch(
            r ->
                map(r.getId(), r.getText(), r.getAtTime(), r.getSent(), r.getCreatedAt()));
  }

  public void markSent(long id) {
    int n = dsl.update(REMINDERS).set(REMINDERS.SENT, true).where(REMINDERS.ID.eq(id)).execute();
    if (n == 0) throw new NotFoundException("Reminder not found: " + id);
  }

  private ReminderDtos.Resp map(
      Long id, String text, java.time.LocalDateTime at, Boolean sent, java.time.LocalDateTime created) {
    return new ReminderDtos.Resp(
        id, text, TimeUtil.toInstant(at), Boolean.TRUE.equals(sent), TimeUtil.toInstant(created));
  }
}
