package org.jdk.workmate.service;

import java.time.Instant;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.jdk.workmate.dto.TodoDtos;
import org.jdk.workmate.exception.NotFoundException;
import org.jdk.workmate.repository.TodoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** 待办业务逻辑：创建、勾选、分页查询、删除 */
@Service
@RequiredArgsConstructor
public class TodoService {

  private final TodoRepository repo;

  @Transactional
  public TodoDtos.Resp create(String title) {
    Long id = repo.create(title);
    return repo.get(id);
  }

  @Transactional
  public void setDone(long id, boolean done) {
    repo.setDone(id, done, Instant.now());
  }

  @Transactional(readOnly = true)
  public List<TodoDtos.Resp> list(Boolean done, int page, int size) {
    return repo.list(done, page, size);
  }

  @Transactional
  public void delete(long id) {
    int n = repo.delete(id);
    if (n == 0) throw new NotFoundException("Todo not found: " + id);
  }
}
