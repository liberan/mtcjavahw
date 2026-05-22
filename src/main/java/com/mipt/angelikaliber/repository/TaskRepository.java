package com.mipt.angelikaliber.repository;

import com.mipt.angelikaliber.model.Priority;
import com.mipt.angelikaliber.model.Task;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    List<Task> findByCompletedAndPriority(boolean completed, Priority priority);

    List<Task> findByCompleted(boolean completed);

    @Query("select t from Task t where t.dueDate between :from and :to")
    List<Task> findTasksDueWithin(@Param("from") LocalDate from, @Param("to") LocalDate to);

    @EntityGraph(attributePaths = "attachments")
    @Query("select distinct t from Task t")
    List<Task> findAllWithAttachments();

    @Query("select t from Task t left join fetch t.attachments where t.id = :id")
    Task findByIdWithAttachments(@Param("id") Long id);
}
