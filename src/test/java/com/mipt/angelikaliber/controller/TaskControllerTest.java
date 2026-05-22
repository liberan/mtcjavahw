package com.mipt.angelikaliber.controller;

import com.mipt.angelikaliber.dto.TaskCreateDto;
import com.mipt.angelikaliber.dto.TaskResponseDto;
import com.mipt.angelikaliber.dto.TaskUpdateDto;
import com.mipt.angelikaliber.model.Priority;
import com.mipt.angelikaliber.model.Task;
import com.mipt.angelikaliber.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class TaskControllerTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private TaskRepository taskRepository;

    @BeforeEach
    void reset() {
        taskRepository.deleteAll();
    }

    private Task saveTask(String title, String description, boolean completed, Priority priority) {
        Task task = new Task(null, title, description, completed);
        task.setPriority(priority);
        return taskRepository.save(task);
    }

    @Test
    void getAllReturnsListAndCountHeader() {
        saveTask("Read", "spec", false, Priority.LOW);
        saveTask("Code", "MVP", true, Priority.HIGH);

        ResponseEntity<TaskResponseDto[]> response =
                restTemplate.getForEntity("/api/tasks", TaskResponseDto[].class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(2);
        assertThat(response.getHeaders().getFirst("X-Total-Count")).isEqualTo("2");
        assertThat(response.getHeaders().getFirst("X-API-Version")).isEqualTo("2.0.0");
    }

    @Test
    void getAllReturnsEmptyList() {
        ResponseEntity<TaskResponseDto[]> response =
                restTemplate.getForEntity("/api/tasks", TaskResponseDto[].class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEmpty();
    }

    @Test
    void getByIdReturnsTask() {
        Task saved = saveTask("Title", "Desc", false, Priority.LOW);

        ResponseEntity<TaskResponseDto> response =
                restTemplate.getForEntity("/api/tasks/" + saved.getId(), TaskResponseDto.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getId()).isEqualTo(saved.getId());
        assertThat(response.getBody().getTitle()).isEqualTo("Title");
    }

    @Test
    void getByIdReturnsNotFound() {
        ResponseEntity<String> response =
                restTemplate.getForEntity("/api/tasks/9999", String.class);

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
        assertThat(response.getBody().getTags()).containsExactlyInAnyOrder("a", "b");
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
        Task saved = saveTask("Old", "Old desc", false, Priority.LOW);

        TaskUpdateDto dto = new TaskUpdateDto();
        dto.setTitle("New");
        dto.setCompleted(true);

        ResponseEntity<TaskResponseDto> response = restTemplate.exchange(
                "/api/tasks/" + saved.getId(), HttpMethod.PUT, new HttpEntity<>(dto), TaskResponseDto.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getId()).isEqualTo(saved.getId());
        assertThat(response.getBody().getTitle()).isEqualTo("New");
        assertThat(response.getBody().isCompleted()).isTrue();
    }

    @Test
    void updateReturnsNotFound() {
        TaskUpdateDto dto = new TaskUpdateDto();
        dto.setTitle("Whatever");

        ResponseEntity<String> response = restTemplate.exchange(
                "/api/tasks/9999", HttpMethod.PUT, new HttpEntity<>(dto), String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void updateRejectsShortTitle() {
        Task saved = saveTask("Old", "Old desc", false, Priority.LOW);
        TaskUpdateDto dto = new TaskUpdateDto();
        dto.setTitle("ab");

        ResponseEntity<String> response = restTemplate.exchange(
                "/api/tasks/" + saved.getId(), HttpMethod.PUT, new HttpEntity<>(dto), String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void updateRejectsPastDueDate() {
        Task saved = saveTask("Old", "Old desc", false, Priority.LOW);
        TaskUpdateDto dto = new TaskUpdateDto();
        dto.setDueDate(LocalDate.now().minusDays(2));

        ResponseEntity<String> response = restTemplate.exchange(
                "/api/tasks/" + saved.getId(), HttpMethod.PUT, new HttpEntity<>(dto), String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void deleteReturnsNoContent() {
        Task saved = saveTask("Title", "Desc", false, Priority.LOW);

        ResponseEntity<Void> response = restTemplate.exchange(
                "/api/tasks/" + saved.getId(), HttpMethod.DELETE, HttpEntity.EMPTY, Void.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        assertThat(taskRepository.findById(saved.getId())).isEmpty();
    }

    @Test
    void deleteReturnsNotFound() {
        ResponseEntity<String> response = restTemplate.exchange(
                "/api/tasks/9999", HttpMethod.DELETE, HttpEntity.EMPTY, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void bulkCompleteMarksTasks() {
        Task one = saveTask("One", "desc", false, Priority.LOW);
        Task two = saveTask("Two", "desc", false, Priority.HIGH);

        ResponseEntity<TaskResponseDto[]> response = restTemplate.postForEntity(
                "/api/tasks/bulk-complete", List.of(one.getId(), two.getId()), TaskResponseDto[].class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(2);
        assertThat(taskRepository.findById(one.getId()).get().isCompleted()).isTrue();
        assertThat(taskRepository.findById(two.getId()).get().isCompleted()).isTrue();
    }

    @Test
    void bulkCompleteRollsBackWhenUnknownId() {
        Task one = saveTask("One", "desc", false, Priority.LOW);

        ResponseEntity<String> response = restTemplate.postForEntity(
                "/api/tasks/bulk-complete", List.of(one.getId(), 9999L), String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(taskRepository.findById(one.getId()).get().isCompleted()).isFalse();
    }
}
