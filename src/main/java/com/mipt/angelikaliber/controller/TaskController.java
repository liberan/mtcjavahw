package com.mipt.angelikaliber.controller;

import com.mipt.angelikaliber.dto.TaskCreateDto;
import com.mipt.angelikaliber.dto.TaskResponseDto;
import com.mipt.angelikaliber.dto.TaskUpdateDto;
import com.mipt.angelikaliber.mapper.TaskMapper;
import com.mipt.angelikaliber.model.Task;
import com.mipt.angelikaliber.service.PrototypeScopedBean;
import com.mipt.angelikaliber.service.RequestScopedBean;
import com.mipt.angelikaliber.service.TaskService;
import com.mipt.angelikaliber.service.TaskStatisticsService;
import com.mipt.angelikaliber.validation.OnCreate;
import com.mipt.angelikaliber.validation.OnUpdate;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
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
@Tag(name = "Tasks", description = "CRUD over the to-do list")
public class TaskController {

    private static final Logger LOGGER = LoggerFactory.getLogger(TaskController.class);

    private final TaskService taskService;
    private final TaskMapper taskMapper;
    private final TaskStatisticsService statisticsService;
    private final ObjectProvider<RequestScopedBean> requestScopedBeanProvider;
    private final ObjectProvider<PrototypeScopedBean> prototypeScopedBeanProvider;

    public TaskController(TaskService taskService,
                          TaskMapper taskMapper,
                          TaskStatisticsService statisticsService,
                          ObjectProvider<RequestScopedBean> requestScopedBeanProvider,
                          ObjectProvider<PrototypeScopedBean> prototypeScopedBeanProvider) {
        this.taskService = taskService;
        this.taskMapper = taskMapper;
        this.statisticsService = statisticsService;
        this.requestScopedBeanProvider = requestScopedBeanProvider;
        this.prototypeScopedBeanProvider = prototypeScopedBeanProvider;
    }

    @Operation(summary = "List all tasks")
    @ApiResponse(responseCode = "200", description = "List of tasks")
    @GetMapping
    public ResponseEntity<List<TaskResponseDto>> findAll() {
        List<Task> tasks = taskService.findAll();
        List<TaskResponseDto> body = tasks.stream().map(taskMapper::toResponseDto).toList();
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Total-Count", String.valueOf(tasks.size()));
        return ResponseEntity.ok().headers(headers).body(body);
    }

    @Operation(summary = "Get a task by id")
    @GetMapping("/{id}")
    public ResponseEntity<TaskResponseDto> findById(@PathVariable Long id) {
        Task task = taskService.getById(id);
        return ResponseEntity.ok(taskMapper.toResponseDto(task));
    }

    @Operation(summary = "Create a new task")
    @ApiResponse(responseCode = "201", description = "Task created")
    @PostMapping
    public ResponseEntity<TaskResponseDto> create(
            @Validated(OnCreate.class) @RequestBody TaskCreateDto dto) {
        Task created = taskService.create(taskMapper.toEntity(dto));
        return ResponseEntity.status(201).body(taskMapper.toResponseDto(created));
    }

    @Operation(summary = "Partially update a task")
    @PutMapping("/{id}")
    public ResponseEntity<TaskResponseDto> update(@PathVariable Long id,
            @Validated(OnUpdate.class) @RequestBody TaskUpdateDto dto) {
        Task existing = taskService.getById(id);
        Task patched = taskMapper.updateEntity(dto, existing);
        if (dto.getCompleted() != null) {
            patched.setCompleted(dto.getCompleted());
        }
        Task updated = taskService.update(id, patched);
        return ResponseEntity.ok(taskMapper.toResponseDto(updated));
    }

    @Operation(summary = "Delete a task")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        taskService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> stats() {
        RequestScopedBean requestBean = requestScopedBeanProvider.getObject();
        PrototypeScopedBean prototypeFirst = prototypeScopedBeanProvider.getObject();
        PrototypeScopedBean prototypeSecond = prototypeScopedBeanProvider.getObject();
        LOGGER.debug("stats: request-id={} primary-source={}",
                requestBean.getRequestId(), statisticsService.describePrimarySource());
        Map<String, Object> body = Map.of(
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
        return ResponseEntity.ok(body);
    }
}
