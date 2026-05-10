package com.mipt.angelikaliber.repository;

import com.mipt.angelikaliber.model.TaskAttachment;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class TaskAttachmentRepository {

    private final ConcurrentHashMap<Long, TaskAttachment> store = new ConcurrentHashMap<>();
    private final AtomicLong sequence = new AtomicLong(0);

    public TaskAttachment save(TaskAttachment attachment) {
        if (attachment.getId() == null) {
            attachment.setId(sequence.incrementAndGet());
        }
        store.put(attachment.getId(), attachment);
        return attachment;
    }

    public Optional<TaskAttachment> findById(Long id) {
        return Optional.ofNullable(store.get(id));
    }

    public List<TaskAttachment> findByTaskId(Long taskId) {
        List<TaskAttachment> result = new ArrayList<>();
        for (TaskAttachment a : store.values()) {
            if (taskId.equals(a.getTaskId())) {
                result.add(a);
            }
        }
        return result;
    }

    public boolean delete(Long id) {
        return store.remove(id) != null;
    }
}
