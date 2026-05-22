package com.mipt.angelikaliber.api;

import com.mipt.angelikaliber.dto.gateway.GatewayTaskDto;
import com.mipt.angelikaliber.service.TasksGatewayService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/tasks")
public class GatewayTaskController {

    private final TasksGatewayService gateway;

    public GatewayTaskController(TasksGatewayService gateway) {
        this.gateway = gateway;
    }

    @PostMapping
    public ResponseEntity<GatewayTaskDto> create(@RequestBody GatewayTaskDto body) {
        TasksGatewayService.CreatedTask created = gateway.createTask(body);
        URI location = created.location();
        ResponseEntity.BodyBuilder builder = ResponseEntity.created(location == null ? URI.create("") : location);
        return builder.body(created.task());
    }

    @GetMapping("/{id}")
    public ResponseEntity<GatewayTaskDto> getOne(@PathVariable Long id) {
        return ResponseEntity.ok(gateway.getTask(id));
    }

    @GetMapping
    public ResponseEntity<List<GatewayTaskDto>> list(@RequestParam(required = false) Boolean completed,
                                                     @RequestParam(required = false) Integer limit) {
        return ResponseEntity.ok(gateway.listTasks(completed, limit));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        gateway.deleteTask(id);
        return ResponseEntity.noContent().build();
    }
}
