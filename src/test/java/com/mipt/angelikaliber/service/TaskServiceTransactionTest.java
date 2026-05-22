package com.mipt.angelikaliber.service;

import com.mipt.angelikaliber.exception.BulkCompleteFailedException;
import com.mipt.angelikaliber.model.Priority;
import com.mipt.angelikaliber.model.Task;
import com.mipt.angelikaliber.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("test")
class TaskServiceTransactionTest {

    @Autowired
    private TaskService taskService;

    @Autowired
    private TaskRepository taskRepository;

    @BeforeEach
    void clean() {
        taskRepository.deleteAll();
    }

    private Task save(String title) {
        Task task = new Task(null, title, "desc", false);
        task.setPriority(Priority.LOW);
        return taskRepository.save(task);
    }

    @Test
    void bulkCompleteMarksAllAsCompleted() {
        Task one = save("a");
        Task two = save("b");

        List<Task> updated = taskService.bulkCompleteTasks(List.of(one.getId(), two.getId()));

        assertThat(updated).hasSize(2);
        assertThat(taskRepository.findById(one.getId()).get().isCompleted()).isTrue();
        assertThat(taskRepository.findById(two.getId()).get().isCompleted()).isTrue();
    }

    @Test
    void bulkCompleteRollsBackWhenUnknownId() {
        Task one = save("a");
        Task two = save("b");

        assertThatThrownBy(() -> taskService.bulkCompleteTasks(List.of(one.getId(), 999999L, two.getId())))
                .isInstanceOf(BulkCompleteFailedException.class);

        assertThat(taskRepository.findById(one.getId()).get().isCompleted()).isFalse();
        assertThat(taskRepository.findById(two.getId()).get().isCompleted()).isFalse();
    }
}
