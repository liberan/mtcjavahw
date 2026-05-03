package com.mipt.angelikaliber.repository;

import com.mipt.angelikaliber.model.Task;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Repository
@Primary
public class InMemoryTaskRepository implements TaskRepository {

    private final ConcurrentHashMap<String, Task> store = new ConcurrentHashMap<>();

    @Override
    public Task create(Task task) {
        task.setId(UUID.randomUUID().toString());
        store.put(task.getId(), task);
        return task;
    }

    @Override
    public List<Task> read() {
        return new ArrayList<>(store.values());
    }

    @Override
    public Optional<Task> read(String id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public Optional<Task> update(String id, Task incoming) {
        return Optional.ofNullable(store.computeIfPresent(id, (existingId, existing) -> {
            existing.setTitle(incoming.getTitle());
            existing.setDescription(incoming.getDescription());
            existing.setCompleted(incoming.isCompleted());
            return existing;
        }));
    }

    @Override
    public boolean delete(String id) {
        return store.remove(id) != null;
    }
}
