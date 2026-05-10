package com.mipt.angelikaliber.repository;

import com.mipt.angelikaliber.model.Task;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
@Primary
public class InMemoryTaskRepository implements TaskRepository {

    private final ConcurrentHashMap<Long, Task> store = new ConcurrentHashMap<>();
    private final AtomicLong sequence = new AtomicLong(0);

    @Override
    public Task create(Task task) {
        long id = sequence.incrementAndGet();
        task.setId(id);
        if (task.getCreatedAt() == null) {
            task.setCreatedAt(LocalDateTime.now());
        }
        store.put(id, task);
        return task;
    }

    @Override
    public List<Task> read() {
        return new ArrayList<>(store.values());
    }

    @Override
    public Optional<Task> read(Long id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public Optional<Task> update(Long id, Task incoming) {
        return Optional.ofNullable(store.computeIfPresent(id, (existingId, existing) -> {
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
            if (incoming.getTags() != null && !incoming.getTags().isEmpty()) {
                existing.setTags(incoming.getTags());
            }
            return existing;
        }));
    }

    @Override
    public boolean delete(Long id) {
        return store.remove(id) != null;
    }
}
