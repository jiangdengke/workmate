package org.jdk.workmate.controller;

import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.jdk.workmate.dto.TodoDtos;
import org.jdk.workmate.service.TodoService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

/** 待办接口 */
@RestController
@RequestMapping("/api/v1/todos")
@RequiredArgsConstructor
public class TodoController {

  private final TodoService service;

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public TodoDtos.Resp create(@RequestBody @Valid TodoDtos.CreateReq req) {
    return service.create(req.title());
  }

  @GetMapping
  public List<TodoDtos.Resp> list(
      @RequestParam(required = false) Boolean done,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "20") int size) {
    return service.list(done, page, size);
  }

  @PatchMapping("/{id}/done")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void setDone(@PathVariable long id, @RequestBody TodoDtos.UpdateDoneReq req) {
    service.setDone(id, req.done());
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void delete(@PathVariable long id) {
    service.delete(id);
  }
}
