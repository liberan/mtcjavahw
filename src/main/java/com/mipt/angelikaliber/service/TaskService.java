package com.mipt.angelikaliber.service;

import com.mipt.angelikaliber.exception.BulkCompleteFailedException;
import com.mipt.angelikaliber.exception.TaskNotFoundException;
import com.mipt.angelikaliber.model.Priority;
import com.mipt.angelikaliber.model.Task;
import com.mipt.angelikaliber.repository.TaskRepository;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class TaskService {

    private static final Logger LOGGER = LoggerFactory.getLogger(TaskService.class);

    private final TaskRepository taskRepository;

    @Value("${app.name:to-do-list-manager}")
    private String appName;

    @Value("${app.version:0.0.1}")
    private String appVersion;

    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    @PostConstruct
    public void init() {
        LOGGER.info("[{} v{}] TaskService is ready, repository={}",
                appName, appVersion, taskRepository.getClass().getSimpleName());
    }

    @PreDestroy
    public void cleanup() {
        LOGGER.info("cleanup: TaskService shutdown");
        try {
            Path stats = Path.of("task-service-stats.log");
            Files.writeString(stats, "tasks-count=" + taskRepository.count() + System.lineSeparator());
        } catch (IOException ex) {
            LOGGER.warn("cleanup: failed to write stats: {}", ex.getMessage());
        }
    }

    @Transactional(readOnly = true)
    public List<Task> findAll() {
        return taskRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Task> findById(Long id) {
        return taskRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public Task getById(Long id) {
        return taskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException("Task not found: " + id));
    }

    @Transactional
    public Task create(Task task) {
        task.setId(null);
        return taskRepository.save(task);
    }

    @Transactional
    public Task update(Long id, Task incoming) {
        Task existing = taskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException("Task not found: " + id));
        if (incoming.getTitle() != null) {
            existing.setTitle(incoming.getTitle());
        }
        if (incoming.getDescription() != null) {
            existing.setDescription(incoming.getDescription());
        }
        existing.setCompleted(incoming.isCompleted());
        if (incoming.getDueDate() != null) {
            existing.setDueDate(incoming.getDueDate());
        }
        if (incoming.getPriority() != null) {
            existing.setPriority(incoming.getPriority());
        }
        if (incoming.getTagsRaw() != null) {
            existing.setTagsRaw(incoming.getTagsRaw());
        }
        return taskRepository.save(existing);
    }

    @Transactional
    public void delete(Long id) {
        if (!taskRepository.existsById(id)) {
            throw new TaskNotFoundException("Task not found: " + id);
        }
        taskRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<Task> findByCompletedAndPriority(boolean completed, Priority priority) {
        return taskRepository.findByCompletedAndPriority(completed, priority);
    }

    @Transactional(readOnly = true)
    public List<Task> findTasksDueWithinWeek() {
        LocalDate today = LocalDate.now();
        return taskRepository.findTasksDueWithin(today, today.plusDays(7));
    }

    @Transactional(readOnly = true)
    public List<Task> findAllWithAttachments() {
        return taskRepository.findAllWithAttachments();
    }

    @Transactional(
            propagation = Propagation.REQUIRED,
            isolation = Isolation.READ_COMMITTED,
            rollbackFor = BulkCompleteFailedException.class,
            timeout = 10
    )
    public List<Task> bulkCompleteTasks(List<Long> ids) {
        List<Task> updated = new ArrayList<>();
        for (Long id : ids) {
            Task task = taskRepository.findById(id)
                    .orElseThrow(() -> new BulkCompleteFailedException("Task not found: " + id));
            task.setCompleted(true);
            updated.add(taskRepository.save(task));
        }
        return updated;
    }

    public String getAppName() {
        return appName;
    }

    public String getAppVersion() {
        return appVersion;
    }
}
