package org.jdk.workmate.service;

import java.time.Instant;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jdk.workmate.dto.ReminderDtos;
import org.jdk.workmate.exception.NotFoundException;
import org.jdk.workmate.repository.ReminderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** 提醒业务逻辑：创建、查询、删除、到点派发（示例打印，可对接推送） */
@Service
@RequiredArgsConstructor
@Slf4j
public class ReminderService {

  private final ReminderRepository repo;

  @Transactional
  public ReminderDtos.Resp create(long userId, String text, Instant at) {
    Long id = repo.create(userId, text, at);
    return repo.get(id);
  }

  @Transactional(readOnly = true)
  public List<ReminderDtos.Resp> listByUser(long userId, String status, int page, int size) {
    return repo.listByUser(userId, status, page, size, Instant.now());
  }

  @Transactional
  public void delete(long userId, long id) {
    int n = repo.delete(userId, id);
    if (n == 0) throw new NotFoundException("Reminder not found: " + id);
  }

  /** 取出到期提醒 -> 触发 -> 标记 sent=true */
  @Transactional
  public List<ReminderDtos.Resp> dispatchDue(int max) {
    var due = repo.listDue(max, Instant.now());
    for (var r : due) {
      // TODO: 在此对接实际通知渠道（邮件/短信/企业微信/钉钉/FCM等）
      log.info("[REMINDER] at={} text={}", r.atTime(), r.text());
      repo.markSent(r.id());
    }
    return due;
  }
}
