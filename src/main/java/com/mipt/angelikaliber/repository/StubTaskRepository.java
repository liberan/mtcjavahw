package com.mipt.angelikaliber.repository;

import com.mipt.angelikaliber.model.Task;
import org.springframework.context.annotation.Bean;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class StubTaskRepository implements TaskRepository {

    private final Map<String, Task> stub = new LinkedHashMap<>();

    public StubTaskRepository() {
        stub.put("task-1", new Task("1", "task1", "Create", true));
        stub.put("task-2", new Task("2", "task2", "Read", true));
        stub.put("task-3", new Task("3", "task3", "Update", false));
        stub.put("task-4", new Task("4", "task4", "Delete", false));
    }

    @Override
    public Task create(Task task) {
        return task;
    }

    @Override
    public List<Task> read() {
        return new ArrayList<>(stub.values());
    }

    @Override
    public Optional<Task> read(String id) {
        return Optional.ofNullable(stub.get(id));
    }

    @Override
    public Optional<Task> update(String id, Task task) {
        return Optional.empty();
    }

    @Override
    public boolean delete(String id) {
        return false;
    }
}
