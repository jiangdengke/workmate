package org.jdk.workmate.controller;

import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.jdk.workmate.dto.NoteDtos;
import org.jdk.workmate.service.NoteService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

/** 笔记接口 */
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class NoteController {

  private final NoteService service;

  @PostMapping("/notes")
  @ResponseStatus(HttpStatus.CREATED)
  public NoteDtos.Resp create(@RequestBody @Valid NoteDtos.CreateReq req) {
    return service.create(req.userName(), req.text());
  }

  @GetMapping("/users/{userName}/notes")
  public List<NoteDtos.Resp> listByUser(
      @PathVariable String userName,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "20") int size) {
    return service.listByUser(userName, page, size);
  }

  @DeleteMapping("/notes/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void delete(@PathVariable long id) {
    service.delete(id);
  }
}
