package org.jdk.workmate;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import org.jdk.workmate.controller.TodoController;
import org.jdk.workmate.dto.TodoDtos;
import org.jdk.workmate.service.TodoService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.reactive.server.WebTestClient;

@WebMvcTest(TodoController.class)
@AutoConfigureWebTestClient
class TodoControllerWebTest {

  @Autowired WebTestClient client;

  @MockBean TodoService service;

  @Test
  void create_should_return_201_and_body() {
    var resp = new TodoDtos.Resp(1L, "写周报", false, Instant.parse("2025-09-06T00:00:00Z"), null);
    Mockito.when(service.create("写周报")).thenReturn(resp);

    client
        .post()
        .uri("/api/v1/todos")
        .contentType(APPLICATION_JSON)
        .bodyValue(Map.of("title", "写周报"))
        .exchange()
        .expectStatus()
        .isCreated()
        .expectHeader()
        .contentTypeCompatibleWith(APPLICATION_JSON)
        .expectBody()
        .jsonPath("$.id")
        .isEqualTo(1)
        .jsonPath("$.title")
        .isEqualTo("写周报")
        .jsonPath("$.done")
        .isEqualTo(false);
  }

  @Test
  void list_should_return_200_and_array() {
    var r1 = new TodoDtos.Resp(1L, "A", false, Instant.parse("2025-09-01T00:00:00Z"), null);
    var r2 =
        new TodoDtos.Resp(
            2L,
            "B",
            true,
            Instant.parse("2025-09-02T00:00:00Z"),
            Instant.parse("2025-09-03T00:00:00Z"));
    Mockito.when(service.list(null, 0, 20)).thenReturn(List.of(r1, r2));

    client
        .get()
        .uri("/api/v1/todos")
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody()
        .jsonPath("$.length()")
        .isEqualTo(2)
        .jsonPath("$[0].title")
        .isEqualTo("A")
        .jsonPath("$[1].done")
        .isEqualTo(true);
  }

  @Test
  void setDone_should_return_204() {
    client
        .patch()
        .uri("/api/v1/todos/{id}/done", 10)
        .contentType(APPLICATION_JSON)
        .bodyValue(Map.of("done", true))
        .exchange()
        .expectStatus()
        .isNoContent();

    Mockito.verify(service).setDone(10L, true);
  }

  @Test
  void delete_should_return_204() {
    client.delete().uri("/api/v1/todos/{id}", 9).exchange().expectStatus().isNoContent();

    Mockito.verify(service).delete(9L);
  }
}
