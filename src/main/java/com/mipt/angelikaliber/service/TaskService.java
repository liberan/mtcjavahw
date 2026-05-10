package com.mipt.angelikaliber.service;

import com.mipt.angelikaliber.exception.TaskNotFoundException;
import com.mipt.angelikaliber.model.Task;
import com.mipt.angelikaliber.repository.TaskRepository;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class TaskService {

    private static final Logger LOGGER = LoggerFactory.getLogger(TaskService.class);

    private final TaskRepository taskRepository;
    private final Map<Long, Task> taskCache = new ConcurrentHashMap<>();

    @Value("${app.name:to-do-list-manager}")
    private String appName;

    @Value("${app.version:0.0.1}")
    private String appVersion;

    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    @PostConstruct
    public void initCache() {
        LOGGER.info("[{} v{}] initCache: warming task cache from {}",
                appName, appVersion, taskRepository.getClass().getSimpleName());
        List<Task> existing = taskRepository.read();
        if (existing != null && existing.isEmpty()) {
            trySeed(new Task(null, "Buy groceries", "Milk, bread, eggs", false));
            trySeed(new Task(null, "Write report", "Quarterly status report", false));
            trySeed(new Task(null, "Workout", "30 min run", true));
        }
        List<Task> reload = taskRepository.read();
        if (reload != null) {
            for (Task task : reload) {
                if (task != null && task.getId() != null) {
                    taskCache.put(task.getId(), task);
                }
            }
        }
        LOGGER.info("initCache: loaded {} tasks into cache", taskCache.size());
    }

    private void trySeed(Task task) {
        Task saved = taskRepository.create(task);
        if (saved != null && saved.getId() != null) {
            taskCache.put(saved.getId(), saved);
        }
    }

    @PreDestroy
    public void cleanup() {
        LOGGER.info("cleanup: tasks in cache before shutdown = {}", taskCache.size());
        try {
            Path stats = Path.of("task-service-stats.log");
            Files.writeString(stats, "tasks-in-cache=" + taskCache.size() + System.lineSeparator());
            LOGGER.info("cleanup: wrote shutdown stats to {}", stats.toAbsolutePath());
        } catch (IOException ex) {
            LOGGER.warn("cleanup: failed to write stats file: {}", ex.getMessage());
        }
        taskCache.clear();
    }

    public List<Task> findAll() {
        return taskRepository.read();
    }

    public Optional<Task> findById(Long id) {
        return taskRepository.read(id);
    }

    public Task getById(Long id) {
        return taskRepository.read(id)
                .orElseThrow(() -> new TaskNotFoundException("Task not found: " + id));
    }

    public Task create(Task task) {
        task.setId(null);
        Task created = taskRepository.create(task);
        taskCache.put(created.getId(), created);
        return created;
    }

    public Task update(Long id, Task incoming) {
        return taskRepository.update(id, incoming)
                .map(task -> {
                    taskCache.put(task.getId(), task);
                    return task;
                })
                .orElseThrow(() -> new TaskNotFoundException("Task not found: " + id));
    }

    public void delete(Long id) {
        boolean removed = taskRepository.delete(id);
        if (!removed) {
            throw new TaskNotFoundException("Task not found: " + id);
        }
        taskCache.remove(id);
    }

    public int cacheSize() {
        return taskCache.size();
    }

    public String getAppName() {
        return appName;
    }

    public String getAppVersion() {
        return appVersion;
    }
}
