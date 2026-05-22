package com.mipt.angelikaliber.repository;

import com.mipt.angelikaliber.model.TaskAttachment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskAttachmentRepository extends JpaRepository<TaskAttachment, Long> {

    @Query("select a from TaskAttachment a where a.task.id = :taskId")
    List<TaskAttachment> findByTaskId(@Param("taskId") Long taskId);
}
