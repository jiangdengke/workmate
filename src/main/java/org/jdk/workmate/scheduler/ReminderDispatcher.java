package org.jdk.workmate.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jdk.workmate.service.ReminderService;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/** 定时扫描到期提醒并触发 */
@Component
@EnableScheduling
@RequiredArgsConstructor
@Slf4j
public class ReminderDispatcher {

  private final ReminderService service;

  /** 每 30 秒检查一次；生产可改为 MQ/延时队列/Quartz */
  @Scheduled(fixedDelayString = "PT30S")
  public void tick() {
    var list = service.dispatchDue(100);
    if (!list.isEmpty()) {
      log.info("Dispatched {} reminders", list.size());
    }
  }
}
