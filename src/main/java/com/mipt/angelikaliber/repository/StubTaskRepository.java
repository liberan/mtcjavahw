package com.mipt.angelikaliber.repository;

import com.mipt.angelikaliber.model.Task;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class StubTaskRepository implements TaskRepository {

    private final Map<Long, Task> stub = new LinkedHashMap<>();

    public StubTaskRepository() {
        stub.put(1L, new Task(1L, "task1", "Create", true));
        stub.put(2L, new Task(2L, "task2", "Read", true));
        stub.put(3L, new Task(3L, "task3", "Update", false));
        stub.put(4L, new Task(4L, "task4", "Delete", false));
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
    public Optional<Task> read(Long id) {
        return Optional.ofNullable(stub.get(id));
    }

    @Override
    public Optional<Task> update(Long id, Task task) {
        return Optional.empty();
    }

    @Override
    public boolean delete(Long id) {
        return false;
    }
}
