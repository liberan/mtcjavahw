package com.mipt.angelikaliber.mapper;

import com.mipt.angelikaliber.dto.TaskCreateDto;
import com.mipt.angelikaliber.dto.TaskResponseDto;
import com.mipt.angelikaliber.dto.TaskUpdateDto;
import com.mipt.angelikaliber.model.Priority;
import com.mipt.angelikaliber.model.Task;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class TaskMapperTest {

    private final TaskMapper mapper = Mappers.getMapper(TaskMapper.class);

    @Test
    void createDtoMapsToEntity() {
        TaskCreateDto dto = new TaskCreateDto();
        dto.setTitle("Title");
        dto.setDescription("Desc");
        dto.setDueDate(LocalDate.of(2030, 1, 1));
        dto.setPriority(Priority.HIGH);
        dto.setTags(Set.of("x", "y"));

        Task task = mapper.toEntity(dto);

        assertThat(task.getId()).isNull();
        assertThat(task.getCreatedAt()).isNull();
        assertThat(task.getTitle()).isEqualTo("Title");
        assertThat(task.getPriority()).isEqualTo(Priority.HIGH);
        assertThat(task.isCompleted()).isFalse();
    }

    @Test
    void updateDtoIgnoresNullFields() {
        Task existing = new Task(1L, "Old", "Old desc", false);
        existing.setCreatedAt(LocalDateTime.of(2020, 1, 1, 0, 0));
        existing.setPriority(Priority.LOW);

        TaskUpdateDto dto = new TaskUpdateDto();
        dto.setTitle("New");

        Task updated = mapper.updateEntity(dto, existing);

        assertThat(updated.getTitle()).isEqualTo("New");
        assertThat(updated.getDescription()).isEqualTo("Old desc");
        assertThat(updated.getPriority()).isEqualTo(Priority.LOW);
        assertThat(updated.getCreatedAt()).isEqualTo(LocalDateTime.of(2020, 1, 1, 0, 0));
        assertThat(updated.getId()).isEqualTo(1L);
    }

    @Test
    void responseDtoCarriesAllFields() {
        Task task = new Task(42L, "T", "D", true);
        task.setCreatedAt(LocalDateTime.of(2025, 1, 1, 10, 0));
        task.setDueDate(LocalDate.of(2026, 1, 1));
        task.setPriority(Priority.MEDIUM);
        task.setTags(Set.of("k"));

        TaskResponseDto dto = mapper.toResponseDto(task);

        assertThat(dto.getId()).isEqualTo(42L);
        assertThat(dto.isCompleted()).isTrue();
        assertThat(dto.getCreatedAt()).isEqualTo(LocalDateTime.of(2025, 1, 1, 10, 0));
        assertThat(dto.getDueDate()).isEqualTo(LocalDate.of(2026, 1, 1));
        assertThat(dto.getPriority()).isEqualTo(Priority.MEDIUM);
        assertThat(dto.getTags()).containsExactly("k");
    }
}
