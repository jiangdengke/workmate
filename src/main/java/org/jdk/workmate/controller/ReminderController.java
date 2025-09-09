package org.jdk.workmate.controller;

import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.jdk.workmate.dto.ReminderDtos;
import org.jdk.workmate.service.ReminderService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

/** 提醒接口 */
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class ReminderController {

  private final ReminderService service;

  @PostMapping("/reminders")
  @ResponseStatus(HttpStatus.CREATED)
  public ReminderDtos.Resp create(@RequestBody @Valid ReminderDtos.CreateReq req) {
    return service.create(org.jdk.workmate.security.CurrentUser.id(), req.text(), req.atTime());
  }

  @GetMapping("/reminders")
  public List<ReminderDtos.Resp> list(
      @RequestParam(defaultValue = "all") String status,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "20") int size) {
    return service.listByUser(org.jdk.workmate.security.CurrentUser.id(), status, page, size);
  }

  @DeleteMapping("/reminders/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void delete(@PathVariable long id) {
    service.delete(org.jdk.workmate.security.CurrentUser.id(), id);
  }
}
