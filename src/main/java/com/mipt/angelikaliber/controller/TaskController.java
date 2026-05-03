package com.mipt.angelikaliber.controller;

import com.mipt.angelikaliber.model.Task;
import com.mipt.angelikaliber.service.PrototypeScopedBean;
import com.mipt.angelikaliber.service.RequestScopedBean;
import com.mipt.angelikaliber.service.TaskService;
import com.mipt.angelikaliber.service.TaskStatisticsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private static final Logger LOGGER = LoggerFactory.getLogger(TaskController.class);

    private final TaskService taskService;
    private final TaskStatisticsService statisticsService;
    private final ObjectProvider<RequestScopedBean> requestScopedBeanProvider;
    private final ObjectProvider<PrototypeScopedBean> prototypeScopedBeanProvider;

    public TaskController(TaskService taskService,
                          TaskStatisticsService statisticsService,
                          ObjectProvider<RequestScopedBean> requestScopedBeanProvider,
                          ObjectProvider<PrototypeScopedBean> prototypeScopedBeanProvider) {
        this.taskService = taskService;
        this.statisticsService = statisticsService;
        this.requestScopedBeanProvider = requestScopedBeanProvider;
        this.prototypeScopedBeanProvider = prototypeScopedBeanProvider;
    }

    @GetMapping
    public List<Task> findAll() {
        return taskService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Task> findById(@PathVariable String id) {
        return taskService.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Task> create(@RequestBody Task task) {
        if (task == null || task.getTitle() == null || task.getTitle().isBlank()) {
            return ResponseEntity.badRequest().build();
        }
        Task created = taskService.create(task);
        return ResponseEntity.status(201).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Task> update(@PathVariable String id, @RequestBody Task task) {
        if (task == null || task.getTitle() == null || task.getTitle().isBlank()) {
            return ResponseEntity.badRequest().build();
        }
        return taskService.update(id, task)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        return taskService.delete(id)
                ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }

    @GetMapping("/stats")
    public Map<String, Object> stats() {
        RequestScopedBean requestBean = requestScopedBeanProvider.getObject();
        PrototypeScopedBean prototypeFirst = prototypeScopedBeanProvider.getObject();
        PrototypeScopedBean prototypeSecond = prototypeScopedBeanProvider.getObject();
        LOGGER.debug("stats: request-id={} primary-source={}",
                requestBean.getRequestId(), statisticsService.describePrimarySource());
        return Map.of(
                "appName", taskService.getAppName(),
                "appVersion", taskService.getAppVersion(),
                "cacheSize", taskService.cacheSize(),
                "countsBySource", statisticsService.countsBySource(),
                "primarySource", statisticsService.describePrimarySource(),
                "stubSource", statisticsService.describeStubSource(),
                "requestId", requestBean.getRequestId(),
                "requestStartedAt", requestBean.getStartedAt().toString(),
                "prototypeIds", List.of(prototypeFirst.getTaskId(), prototypeSecond.getTaskId())
        );
    }
}
