package com.mipt.angelikaliber.repository;

import com.mipt.angelikaliber.model.Task;

import java.util.List;
import java.util.Optional;

public interface TaskRepository {

    Task create(Task task);

    List<Task> read();

    Optional<Task> read(Long id);

    Optional<Task> update(Long id, Task task);

    boolean delete(Long id);
}
