package com.mipt.angelikaliber.controller;

import com.mipt.angelikaliber.dto.TaskResponseDto;
import com.mipt.angelikaliber.mapper.TaskMapper;
import com.mipt.angelikaliber.service.FavoritesService;
import com.mipt.angelikaliber.service.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/favorites")
@Tag(name = "Favorites", description = "Session-backed favorite tasks")
public class FavoritesController {

    private final FavoritesService favoritesService;
    private final TaskService taskService;
    private final TaskMapper taskMapper;

    public FavoritesController(FavoritesService favoritesService,
                               TaskService taskService,
                               TaskMapper taskMapper) {
        this.favoritesService = favoritesService;
        this.taskService = taskService;
        this.taskMapper = taskMapper;
    }

    @Operation(summary = "Add a task to favorites")
    @PostMapping("/{taskId}")
    public ResponseEntity<Void> add(@PathVariable Long taskId, HttpSession session) {
        taskService.getById(taskId);
        favoritesService.add(session, taskId);
        return ResponseEntity.status(201).build();
    }

    @Operation(summary = "Remove a task from favorites")
    @DeleteMapping("/{taskId}")
    public ResponseEntity<Void> remove(@PathVariable Long taskId, HttpSession session) {
        boolean removed = favoritesService.remove(session, taskId);
        return removed ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }

    @Operation(summary = "List favorite tasks")
    @GetMapping
    public ResponseEntity<List<TaskResponseDto>> list(HttpSession session) {
        List<TaskResponseDto> body = new ArrayList<>();
        for (Long id : favoritesService.getFavorites(session)) {
            taskService.findById(id).ifPresent(t -> body.add(taskMapper.toResponseDto(t)));
        }
        return ResponseEntity.ok(body);
    }
}
