package com.mipt.angelikaliber.controller;

import com.mipt.angelikaliber.dto.TaskCreateDto;
import com.mipt.angelikaliber.dto.TaskResponseDto;
import com.mipt.angelikaliber.dto.TaskUpdateDto;
import com.mipt.angelikaliber.model.Priority;
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

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

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
        AtomicLong seq = new AtomicLong(100);
        when(taskRepository.create(any(Task.class))).thenAnswer(invocation -> {
            Task incoming = invocation.getArgument(0);
            incoming.setId(seq.incrementAndGet());
            return incoming;
        });
        when(taskRepository.update(any(Long.class), any(Task.class))).thenReturn(Optional.empty());
    }

    @Test
    void getAllReturnsListAndCountHeader() {
        Task one = new Task(1L, "Read", "spec", false);
        Task two = new Task(2L, "Code", "MVP", true);
        when(taskRepository.read()).thenReturn(List.of(one, two));

        ResponseEntity<TaskResponseDto[]> response =
                restTemplate.getForEntity("/api/tasks", TaskResponseDto[].class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(2);
        assertThat(response.getHeaders().getFirst("X-Total-Count")).isEqualTo("2");
        assertThat(response.getHeaders().getFirst("X-API-Version")).isEqualTo("2.0.0");
    }

    @Test
    void getAllReturnsEmptyList() {
        when(taskRepository.read()).thenReturn(List.of());

        ResponseEntity<TaskResponseDto[]> response =
                restTemplate.getForEntity("/api/tasks", TaskResponseDto[].class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEmpty();
    }

    @Test
    void getByIdReturnsTask() {
        Task task = new Task(7L, "Title", "Desc", false);
        when(taskRepository.read(7L)).thenReturn(Optional.of(task));

        ResponseEntity<TaskResponseDto> response =
                restTemplate.getForEntity("/api/tasks/7", TaskResponseDto.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getId()).isEqualTo(7L);
        assertThat(response.getBody().getTitle()).isEqualTo("Title");
    }

    @Test
    void getByIdReturnsNotFound() {
        when(taskRepository.read(404L)).thenReturn(Optional.empty());

        ResponseEntity<String> response =
                restTemplate.getForEntity("/api/tasks/404", String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).contains("\"status\":404");
    }

    @Test
    void createReturnsCreated() {
        TaskCreateDto dto = new TaskCreateDto();
        dto.setTitle("New task");
        dto.setDescription("details");
        dto.setDueDate(LocalDate.now().plusDays(3));
        dto.setPriority(Priority.HIGH);
        dto.setTags(Set.of("a", "b"));

        ResponseEntity<TaskResponseDto> response =
                restTemplate.postForEntity("/api/tasks", dto, TaskResponseDto.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody().getId()).isNotNull();
        assertThat(response.getBody().getTitle()).isEqualTo("New task");
        assertThat(response.getBody().getPriority()).isEqualTo(Priority.HIGH);
    }

    @Test
    void createReturnsBadRequestWhenTitleMissing() {
        TaskCreateDto dto = new TaskCreateDto();
        dto.setDueDate(LocalDate.now().plusDays(1));
        dto.setPriority(Priority.LOW);

        ResponseEntity<String> response =
                restTemplate.postForEntity("/api/tasks", dto, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).contains("title");
    }

    @Test
    void createReturnsBadRequestWhenDueDateInPast() {
        TaskCreateDto dto = new TaskCreateDto();
        dto.setTitle("Past");
        dto.setDueDate(LocalDate.now().minusDays(1));
        dto.setPriority(Priority.LOW);

        ResponseEntity<String> response =
                restTemplate.postForEntity("/api/tasks", dto, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).contains("dueDate");
    }

    @Test
    void createReturnsBadRequestWhenTooManyTags() {
        TaskCreateDto dto = new TaskCreateDto();
        dto.setTitle("Title");
        dto.setDueDate(LocalDate.now().plusDays(1));
        dto.setPriority(Priority.LOW);
        dto.setTags(Set.of("a", "b", "c", "d", "e", "f"));

        ResponseEntity<String> response =
                restTemplate.postForEntity("/api/tasks", dto, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void updateReturnsUpdatedTask() {
        Task existing = new Task(9L, "Old", "Old desc", false);
        when(taskRepository.read(9L)).thenReturn(Optional.of(existing));
        Task updated = new Task(9L, "New", "New desc", true);
        when(taskRepository.update(eq(9L), any(Task.class))).thenReturn(Optional.of(updated));

        TaskUpdateDto dto = new TaskUpdateDto();
        dto.setTitle("New");
        dto.setCompleted(true);

        ResponseEntity<TaskResponseDto> response = restTemplate.exchange(
                "/api/tasks/9", HttpMethod.PUT, new HttpEntity<>(dto), TaskResponseDto.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getId()).isEqualTo(9L);
        assertThat(response.getBody().getTitle()).isEqualTo("New");
        assertThat(response.getBody().isCompleted()).isTrue();
    }

    @Test
    void updateReturnsNotFound() {
        when(taskRepository.read(404L)).thenReturn(Optional.empty());

        TaskUpdateDto dto = new TaskUpdateDto();
        dto.setTitle("Whatever");

        ResponseEntity<String> response = restTemplate.exchange(
                "/api/tasks/404", HttpMethod.PUT, new HttpEntity<>(dto), String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void updateRejectsShortTitle() {
        TaskUpdateDto dto = new TaskUpdateDto();
        dto.setTitle("ab");

        ResponseEntity<String> response = restTemplate.exchange(
                "/api/tasks/9", HttpMethod.PUT, new HttpEntity<>(dto), String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void updateRejectsPastDueDate() {
        TaskUpdateDto dto = new TaskUpdateDto();
        dto.setDueDate(LocalDate.now().minusDays(2));

        ResponseEntity<String> response = restTemplate.exchange(
                "/api/tasks/9", HttpMethod.PUT, new HttpEntity<>(dto), String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void deleteReturnsNoContent() {
        when(taskRepository.delete(7L)).thenReturn(true);

        ResponseEntity<Void> response = restTemplate.exchange(
                "/api/tasks/7", HttpMethod.DELETE, HttpEntity.EMPTY, Void.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }

    @Test
    void deleteReturnsNotFound() {
        when(taskRepository.delete(404L)).thenReturn(false);

        ResponseEntity<String> response = restTemplate.exchange(
                "/api/tasks/404", HttpMethod.DELETE, HttpEntity.EMPTY, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }
}
