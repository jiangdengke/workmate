package org.jdk.workmate.repository;

import static org.jooq.generated.tables.Notes.NOTES;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.jdk.workmate.dto.NoteDtos;
import org.jdk.workmate.exception.NotFoundException;
import org.jdk.workmate.util.TimeUtil;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

/** 笔记数据访问层 */
@Repository
@RequiredArgsConstructor
public class NoteRepository {

  private final DSLContext dsl;

  public Long create(String userName, String text) {
    var rec =
        dsl.insertInto(NOTES)
            .set(NOTES.USER_NAME, userName)
            .set(NOTES.TEXT, text)
            .returning(NOTES.ID)
            .fetchOne();

    if (rec == null) throw new IllegalStateException("Insert note failed");
    return rec.get(NOTES.ID);
  }

  public int delete(long id) {
    return dsl.deleteFrom(NOTES).where(NOTES.ID.eq(id)).execute();
  }

  public NoteDtos.Resp get(long id) {
    var r = dsl.selectFrom(NOTES).where(NOTES.ID.eq(id)).fetchOne();
    if (r == null) throw new NotFoundException("Note not found: " + id);
    return new NoteDtos.Resp(
        r.get(NOTES.ID),
        r.get(NOTES.USER_NAME),
        r.get(NOTES.TEXT),
        TimeUtil.toInstant(r.get(NOTES.CREATED_AT)));
  }

  public List<NoteDtos.Resp> listByUser(String userName, int page, int size) {
    return dsl.selectFrom(NOTES)
        .where(NOTES.USER_NAME.eq(userName))
        .orderBy(NOTES.CREATED_AT.desc())
        .limit(size)
        .offset(page * size)
        .fetch(
            r ->
                new NoteDtos.Resp(
                    r.get(NOTES.ID),
                    r.get(NOTES.USER_NAME),
                    r.get(NOTES.TEXT),
                    TimeUtil.toInstant(r.get(NOTES.CREATED_AT))));
  }
}
