package com.mipt.angelikaliber.service;

import com.mipt.angelikaliber.exception.AttachmentNotFoundException;
import com.mipt.angelikaliber.model.TaskAttachment;
import com.mipt.angelikaliber.repository.TaskAttachmentRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class AttachmentService {

    private final TaskAttachmentRepository repository;
    private final TaskService taskService;
    private final Path storageRoot;

    public AttachmentService(TaskAttachmentRepository repository,
                             TaskService taskService,
                             @Value("${app.upload-dir:uploads}") String storageDir) {
        this.repository = repository;
        this.taskService = taskService;
        this.storageRoot = Paths.get(storageDir).toAbsolutePath().normalize();
    }

    @PostConstruct
    public void init() throws IOException {
        Files.createDirectories(storageRoot);
    }

    public TaskAttachment storeAttachment(Long taskId, MultipartFile file) throws IOException {
        taskService.getById(taskId);

        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Uploaded file is empty");
        }

        String original = file.getOriginalFilename() == null ? "file" : file.getOriginalFilename();
        String ext = "";
        int dot = original.lastIndexOf('.');
        if (dot >= 0) {
            ext = original.substring(dot);
        }
        String stored = UUID.randomUUID() + ext;
        Path target = storageRoot.resolve(stored);

        try (InputStream in = file.getInputStream()) {
            Files.copy(in, target, StandardCopyOption.REPLACE_EXISTING);
        }

        TaskAttachment attachment = new TaskAttachment();
        attachment.setTaskId(taskId);
        attachment.setFileName(original);
        attachment.setStoredFileName(stored);
        attachment.setContentType(file.getContentType());
        attachment.setSize(file.getSize());
        attachment.setUploadedAt(LocalDateTime.now());
        return repository.save(attachment);
    }

    public TaskAttachment getAttachment(Long attachmentId) {
        return repository.findById(attachmentId)
                .orElseThrow(() -> new AttachmentNotFoundException("Attachment not found: " + attachmentId));
    }

    public List<TaskAttachment> getByTask(Long taskId) {
        taskService.getById(taskId);
        return repository.findByTaskId(taskId);
    }

    public Resource loadAsResource(Long attachmentId) {
        TaskAttachment attachment = getAttachment(attachmentId);
        Path file = storageRoot.resolve(attachment.getStoredFileName());
        try {
            Resource resource = new UrlResource(file.toUri());
            if (!resource.exists() || !resource.isReadable()) {
                throw new AttachmentNotFoundException("File missing on disk: " + attachmentId);
            }
            return resource;
        } catch (MalformedURLException ex) {
            throw new AttachmentNotFoundException("Bad file path for: " + attachmentId);
        }
    }

    public void deleteAttachment(Long attachmentId) {
        TaskAttachment attachment = getAttachment(attachmentId);
        Path file = storageRoot.resolve(attachment.getStoredFileName());
        try {
            Files.deleteIfExists(file);
        } catch (IOException ignored) {
        }
        repository.delete(attachmentId);
    }
}
