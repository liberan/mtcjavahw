package com.mipt.angelikaliber.controller;

import com.mipt.angelikaliber.model.Task;
import com.mipt.angelikaliber.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("dev")
class TaskControllerTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @MockitoBean(name = "inMemoryTaskRepository")
    private TaskRepository taskRepository;

    @BeforeEach
    void resetMock() {
        Mockito.reset(taskRepository);
        when(taskRepository.read()).thenReturn(new ArrayList<>());
        when(taskRepository.create(any(Task.class))).thenAnswer(invocation -> {
            Task incoming = invocation.getArgument(0);
            incoming.setId("mock-" + UUID.randomUUID());
            return incoming;
        });
        when(taskRepository.update(any(String.class), any(Task.class))).thenReturn(Optional.empty());
    }

    @Test
    void getAllReturnsListOfTasks() {
        Task one = new Task("t1", "Read", "spec", false);
        Task two = new Task("t2", "Code", "MVP", true);
        when(taskRepository.read()).thenReturn(List.of(one, two));

        ResponseEntity<Task[]> response = restTemplate.getForEntity("/api/tasks", Task[].class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).containsExactlyInAnyOrder(one, two);
    }

    @Test
    void getAllReturnsEmptyListWhenRepositoryIsEmpty() {
        when(taskRepository.read()).thenReturn(List.of());

        ResponseEntity<Task[]> response = restTemplate.getForEntity("/api/tasks", Task[].class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEmpty();
    }

    @Test
    void getByIdReturnsTaskWhenFound() {
        Task task = new Task("t-1", "Title", "Desc", false);
        when(taskRepository.read("t-1")).thenReturn(Optional.of(task));

        ResponseEntity<Task> response = restTemplate.getForEntity("/api/tasks/t-1", Task.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(task);
    }

    @Test
    void getByIdReturnsNotFoundWhenMissing() {
        when(taskRepository.read("missing")).thenReturn(Optional.empty());

        ResponseEntity<Task> response = restTemplate.getForEntity("/api/tasks/missing", Task.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void createReturnsCreatedTask() {
        Task payload = new Task(null, "New task", "details", false);

        ResponseEntity<Task> response = restTemplate.postForEntity("/api/tasks", payload, Task.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getId()).isNotBlank();
        assertThat(response.getBody().getTitle()).isEqualTo("New task");
    }

    @Test
    void createReturnsBadRequestWhenTitleMissing() {
        Task payload = new Task(null, "  ", "no title", false);

        ResponseEntity<Task> response = restTemplate.postForEntity("/api/tasks", payload, Task.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void updateReturnsUpdatedTaskWhenFound() {
        Task updated = new Task("t-9", "New", "New desc", true);
        when(taskRepository.update(eq("t-9"), any(Task.class))).thenReturn(Optional.of(updated));

        Task payload = new Task(null, "New", "New desc", true);
        ResponseEntity<Task> response = restTemplate.exchange(
                "/api/tasks/t-9", HttpMethod.PUT, new HttpEntity<>(payload), Task.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getId()).isEqualTo("t-9");
        assertThat(response.getBody().getTitle()).isEqualTo("New");
        assertThat(response.getBody().isCompleted()).isTrue();
    }

    @Test
    void updateReturnsNotFoundWhenMissing() {
        when(taskRepository.update(eq("missing"), any(Task.class))).thenReturn(Optional.empty());

        Task payload = new Task(null, "Whatever", "x", false);
        ResponseEntity<Task> response = restTemplate.exchange(
                "/api/tasks/missing", HttpMethod.PUT, new HttpEntity<>(payload), Task.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void deleteReturnsNoContentWhenRemoved() {
        when(taskRepository.delete("t-7")).thenReturn(true);

        ResponseEntity<Void> response = restTemplate.exchange(
                "/api/tasks/t-7", HttpMethod.DELETE, HttpEntity.EMPTY, Void.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }

    @Test
    void deleteReturnsNotFoundWhenMissing() {
        when(taskRepository.delete(eq("missing"))).thenReturn(false);

        ResponseEntity<Void> response = restTemplate.exchange(
                "/api/tasks/missing", HttpMethod.DELETE, HttpEntity.EMPTY, Void.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }
}
