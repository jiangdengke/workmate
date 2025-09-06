package org.jdk.workmate.repository;

import static org.jooq.generated.tables.Todos.TODOS;

import java.time.Instant;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.jdk.workmate.dto.TodoDtos;
import org.jdk.workmate.exception.NotFoundException;
import org.jdk.workmate.util.TimeUtil;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

/** 待办数据访问层 */
@Repository
@RequiredArgsConstructor
public class TodoRepository {

  private final DSLContext dsl;

  public Long create(String title) {
    return dsl.insertInto(TODOS)
        .set(TODOS.TITLE, title)
        .returningResult(TODOS.ID)
        .fetchOne(TODOS.ID, Long.class);
  }

  public void setDone(long id, boolean done, Instant now) {
    int n =
        dsl.update(TODOS)
            .set(TODOS.DONE, done)
            .set(TODOS.DONE_AT, done ? TimeUtil.toLdt(now) : null)
            .where(TODOS.ID.eq(id))
            .execute();
    if (n == 0) throw new NotFoundException("Todo not found: " + id);
  }

  public int delete(long id) {
    return dsl.deleteFrom(TODOS).where(TODOS.ID.eq(id)).execute();
  }

  public TodoDtos.Resp get(long id) {
    var r = dsl.selectFrom(TODOS).where(TODOS.ID.eq(id)).fetchOne();
    if (r == null) throw new NotFoundException("Todo not found: " + id);
    return map(r.getId(), r.getTitle(), r.getDone(), r.getCreatedAt(), r.getDoneAt());
  }

  public List<TodoDtos.Resp> list(Boolean done, int page, int size) {
    var sel = dsl.selectFrom(TODOS);
    if (done != null) sel.where(TODOS.DONE.eq(done));
    return sel.orderBy(TODOS.CREATED_AT.desc())
        .limit(size)
        .offset(page * size)
        .fetch(r -> map(r.getId(), r.getTitle(), r.getDone(), r.getCreatedAt(), r.getDoneAt()));
  }

  private TodoDtos.Resp map(
      Long id,
      String title,
      Boolean done,
      java.time.LocalDateTime createdAt,
      java.time.LocalDateTime doneAt) {
    return new TodoDtos.Resp(
        id,
        title,
        Boolean.TRUE.equals(done),
        TimeUtil.toInstant(createdAt),
        TimeUtil.toInstant(doneAt));
  }
}
