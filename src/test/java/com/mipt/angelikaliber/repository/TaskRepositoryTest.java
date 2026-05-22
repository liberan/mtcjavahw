package com.mipt.angelikaliber.repository;

import com.mipt.angelikaliber.model.Priority;
import com.mipt.angelikaliber.model.Task;
import com.mipt.angelikaliber.model.TaskAttachment;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class TaskRepositoryTest {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private TaskAttachmentRepository attachmentRepository;

    @Autowired
    private TestEntityManager entityManager;

    @BeforeEach
    void cleanDb() {
        attachmentRepository.deleteAll();
        taskRepository.deleteAll();
    }

    @Test
    void saveAndFindById() {
        Task task = new Task(null, "Saved", "Body", false);
        task.setPriority(Priority.HIGH);
        Task saved = taskRepository.save(task);

        Task found = taskRepository.findById(saved.getId()).orElseThrow();
        assertThat(found.getTitle()).isEqualTo("Saved");
        assertThat(found.getPriority()).isEqualTo(Priority.HIGH);
        assertThat(found.getCreatedAt()).isNotNull();
        assertThat(found.getUpdatedAt()).isNotNull();
    }

    @Test
    void findByCompletedAndPriorityReturnsMatch() {
        Task one = new Task(null, "Done High", "x", true);
        one.setPriority(Priority.HIGH);
        Task two = new Task(null, "Done Low", "x", true);
        two.setPriority(Priority.LOW);
        Task three = new Task(null, "Open High", "x", false);
        three.setPriority(Priority.HIGH);
        taskRepository.save(one);
        taskRepository.save(two);
        taskRepository.save(three);

        List<Task> doneHigh = taskRepository.findByCompletedAndPriority(true, Priority.HIGH);

        assertThat(doneHigh).hasSize(1);
        assertThat(doneHigh.get(0).getTitle()).isEqualTo("Done High");
    }

    @Test
    void findTasksDueWithinReturnsTasksInRange() {
        Task soon = new Task(null, "Soon", "x", false);
        soon.setDueDate(LocalDate.now().plusDays(3));
        Task later = new Task(null, "Later", "x", false);
        later.setDueDate(LocalDate.now().plusDays(20));
        taskRepository.save(soon);
        taskRepository.save(later);

        List<Task> result = taskRepository.findTasksDueWithin(
                LocalDate.now(), LocalDate.now().plusDays(7));

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTitle()).isEqualTo("Soon");
    }

    @Test
    void savingAttachmentLinksToTaskAndCascadeRemoves() {
        Task task = new Task(null, "Parent", "x", false);
        Task saved = taskRepository.save(task);

        TaskAttachment attachment = new TaskAttachment();
        attachment.setTask(saved);
        attachment.setFileName("file.txt");
        attachment.setStoredFileName("uuid.txt");
        attachment.setSize(10);
        attachmentRepository.save(attachment);

        entityManager.flush();
        entityManager.clear();

        List<TaskAttachment> found = attachmentRepository.findByTaskId(saved.getId());
        assertThat(found).hasSize(1);
        assertThat(found.get(0).getFileName()).isEqualTo("file.txt");

        taskRepository.deleteById(saved.getId());
        entityManager.flush();

        assertThat(attachmentRepository.findByTaskId(saved.getId())).isEmpty();
    }

    @Test
    void findAllWithAttachmentsAvoidsNplusOne() {
        Task task = new Task(null, "WithFiles", "x", false);
        Task saved = taskRepository.save(task);

        TaskAttachment a1 = new TaskAttachment();
        a1.setTask(saved);
        a1.setFileName("a.txt");
        a1.setStoredFileName("a-uuid");
        a1.setSize(1);
        TaskAttachment a2 = new TaskAttachment();
        a2.setTask(saved);
        a2.setFileName("b.txt");
        a2.setStoredFileName("b-uuid");
        a2.setSize(2);
        attachmentRepository.save(a1);
        attachmentRepository.save(a2);

        entityManager.flush();
        entityManager.clear();

        List<Task> tasks = taskRepository.findAllWithAttachments();

        assertThat(tasks).hasSize(1);
        assertThat(tasks.get(0).getAttachments()).hasSize(2);
    }
}
