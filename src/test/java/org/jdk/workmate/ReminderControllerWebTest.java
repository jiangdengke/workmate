package org.jdk.workmate;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import org.jdk.workmate.controller.ReminderController;
import org.jdk.workmate.dto.ReminderDtos;
import org.jdk.workmate.service.ReminderService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.reactive.server.WebTestClient;

@WebMvcTest(ReminderController.class)
@AutoConfigureWebTestClient
class ReminderControllerWebTest {

  @Autowired WebTestClient client;

  @MockBean ReminderService service;

  @Test
  void create_should_return_201() {
    var at = Instant.parse("2025-09-10T01:00:00Z");
    var resp =
        new ReminderDtos.Resp(
            1L, "alice", "meet", at, false, Instant.parse("2025-09-06T00:00:00Z"));
    Mockito.when(service.create("alice", "meet", at)).thenReturn(resp);

    client
        .post()
        .uri("/api/v1/reminders")
        .contentType(APPLICATION_JSON)
        .bodyValue(Map.of("userName", "alice", "text", "meet", "atTime", "2025-09-10T01:00:00Z"))
        .exchange()
        .expectStatus()
        .isCreated()
        .expectBody()
        .jsonPath("$.userName")
        .isEqualTo("alice")
        .jsonPath("$.sent")
        .isEqualTo(false);
  }

  @Test
  void list_should_return_200() {
    var r =
        new ReminderDtos.Resp(
            2L,
            "bob",
            "call",
            Instant.parse("2025-09-09T00:00:00Z"),
            false,
            Instant.parse("2025-09-06T00:00:00Z"));
    Mockito.when(service.listByUser(eq("bob"), eq("all"), anyInt(), anyInt()))
        .thenReturn(List.of(r));

    client
        .get()
        .uri("/api/v1/users/{u}/reminders?status=all&page=0&size=20", "bob")
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody()
        .jsonPath("$[0].text")
        .isEqualTo("call");
  }

  @Test
  void delete_should_return_204() {
    client.delete().uri("/api/v1/reminders/{id}", 3).exchange().expectStatus().isNoContent();

    Mockito.verify(service).delete(3L);
  }
}
