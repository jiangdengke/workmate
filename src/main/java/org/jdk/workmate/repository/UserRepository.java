package org.jdk.workmate.repository;

import static org.jooq.generated.tables.Users.USERS;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class UserRepository {
  private final DSLContext dsl;

  public Long create(String userName, String password, String displayName, String email) {
    return dsl.insertInto(USERS)
        .set(USERS.USER_NAME, userName)
        .set(USERS.PASSWORD, password)
        .set(USERS.DISPLAY_NAME, displayName)
        .set(USERS.EMAIL, email)
        .returningResult(USERS.ID)
        .fetchOne(USERS.ID, Long.class);
  }

  public Optional<UserRecord> findByUserName(String userName) {
    var r = dsl.selectFrom(USERS).where(USERS.USER_NAME.eq(userName)).fetchOne();
    return Optional.ofNullable(r).map(
        it -> new UserRecord(it.getId(), it.getUserName(), it.getPassword(), it.getDisplayName(), it.getEmail()));
  }

  public Optional<UserRecord> findById(long id) {
    var r = dsl.selectFrom(USERS).where(USERS.ID.eq(id)).fetchOne();
    return Optional.ofNullable(r).map(
        it -> new UserRecord(it.getId(), it.getUserName(), it.getPassword(), it.getDisplayName(), it.getEmail()));
  }

  public Optional<UserRecord> findByEmail(String email) {
    var r = dsl.selectFrom(USERS).where(USERS.EMAIL.eq(email)).fetchOne();
    return Optional.ofNullable(r).map(
        it -> new UserRecord(it.getId(), it.getUserName(), it.getPassword(), it.getDisplayName(), it.getEmail()));
  }

  public record UserRecord(Long id, String userName, String password, String displayName, String email) {}
}
