package org.jdk.workmate.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.jdk.workmate.dto.NoteDtos;
import org.jdk.workmate.exception.NotFoundException;
import org.jdk.workmate.repository.NoteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** 笔记业务逻辑：创建、按用户分页查询、删除 */
@Service
@RequiredArgsConstructor
public class NoteService {

  private final NoteRepository repo;

  @Transactional
  public NoteDtos.Resp create(long userId, String text) {
    Long id = repo.create(userId, text);
    return repo.get(id);
  }

  @Transactional(readOnly = true)
  public List<NoteDtos.Resp> listByUser(long userId, int page, int size) {
    return repo.listByUser(userId, page, size);
  }

  @Transactional
  public void delete(long userId, long id) {
    int n = repo.delete(userId, id);
    if (n == 0) throw new NotFoundException("Note not found: " + id);
  }
}
