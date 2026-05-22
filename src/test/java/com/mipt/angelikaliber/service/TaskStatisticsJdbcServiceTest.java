package com.mipt.angelikaliber.service;

import com.mipt.angelikaliber.dto.PriorityCountDto;
import com.mipt.angelikaliber.model.Priority;
import com.mipt.angelikaliber.model.Task;
import com.mipt.angelikaliber.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class TaskStatisticsJdbcServiceTest {

    @Autowired
    private TaskStatisticsJdbcService statisticsService;

    @Autowired
    private TaskRepository taskRepository;

    @BeforeEach
    void clean() {
        taskRepository.deleteAll();
    }

    private void save(String title, Priority priority) {
        Task task = new Task(null, title, "x", false);
        task.setPriority(priority);
        taskRepository.save(task);
    }

    @Test
    void getTasksCountByPriorityGroupsCorrectly() {
        save("h1", Priority.HIGH);
        save("h2", Priority.HIGH);
        save("l1", Priority.LOW);

        List<PriorityCountDto> result = statisticsService.getTasksCountByPriority();

        Map<String, Long> byPriority = result.stream()
                .collect(Collectors.toMap(PriorityCountDto::getPriority, PriorityCountDto::getCount));

        assertThat(byPriority).containsEntry("HIGH", 2L);
        assertThat(byPriority).containsEntry("LOW", 1L);
    }
}
