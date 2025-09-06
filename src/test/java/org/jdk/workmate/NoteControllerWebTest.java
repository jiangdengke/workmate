package org.jdk.workmate;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import org.jdk.workmate.controller.NoteController;
import org.jdk.workmate.dto.NoteDtos;
import org.jdk.workmate.service.NoteService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.reactive.server.WebTestClient;

@WebMvcTest(NoteController.class)
@AutoConfigureWebTestClient
class NoteControllerWebTest {

  @Autowired WebTestClient client;

  @MockBean NoteService service;

  @Test
  void create_should_return_201() {
    var resp = new NoteDtos.Resp(1L, "alice", "hello", Instant.parse("2025-09-06T00:00:00Z"));
    Mockito.when(service.create("alice", "hello")).thenReturn(resp);

    client
        .post()
        .uri("/api/v1/notes")
        .contentType(APPLICATION_JSON)
        .bodyValue(Map.of("userName", "alice", "text", "hello"))
        .exchange()
        .expectStatus()
        .isCreated()
        .expectBody()
        .jsonPath("$.id")
        .isEqualTo(1)
        .jsonPath("$.userName")
        .isEqualTo("alice");
  }

  @Test
  void listByUser_should_return_200() {
    var r = new NoteDtos.Resp(1L, "bob", "x", Instant.parse("2025-09-01T00:00:00Z"));
    Mockito.when(service.listByUser(eq("bob"), anyInt(), anyInt())).thenReturn(List.of(r));

    client
        .get()
        .uri("/api/v1/users/{u}/notes?page=0&size=20", "bob")
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody()
        .jsonPath("$[0].userName")
        .isEqualTo("bob");
  }

  @Test
  void delete_should_return_204() {
    client.delete().uri("/api/v1/notes/{id}", 7).exchange().expectStatus().isNoContent();

    Mockito.verify(service).delete(7L);
  }
}
