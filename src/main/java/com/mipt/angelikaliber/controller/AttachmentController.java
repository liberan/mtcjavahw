package com.mipt.angelikaliber.controller;

import com.mipt.angelikaliber.dto.AttachmentResponseDto;
import com.mipt.angelikaliber.model.TaskAttachment;
import com.mipt.angelikaliber.service.AttachmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api")
@Tag(name = "Attachments", description = "Upload and download files for tasks")
public class AttachmentController {

    private final AttachmentService attachmentService;

    public AttachmentController(AttachmentService attachmentService) {
        this.attachmentService = attachmentService;
    }

    @Operation(summary = "Upload a file for a task")
    @PostMapping(value = "/tasks/{taskId}/attachments",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<AttachmentResponseDto> upload(@PathVariable Long taskId,
                                                        @RequestParam("file") MultipartFile file)
            throws IOException {
        TaskAttachment saved = attachmentService.storeAttachment(taskId, file);
        return ResponseEntity.status(201).body(toDto(saved));
    }

    @Operation(summary = "List attachments for a task")
    @GetMapping("/tasks/{taskId}/attachments")
    public ResponseEntity<List<AttachmentResponseDto>> listForTask(@PathVariable Long taskId) {
        List<AttachmentResponseDto> body = attachmentService.getByTask(taskId)
                .stream().map(this::toDto).toList();
        return ResponseEntity.ok(body);
    }

    @Operation(summary = "Download an attachment")
    @GetMapping("/attachments/{attachmentId}")
    public ResponseEntity<Resource> download(@PathVariable Long attachmentId) {
        TaskAttachment meta = attachmentService.getAttachment(attachmentId);
        Resource resource = attachmentService.loadAsResource(attachmentId);
        String contentType = meta.getContentType() == null
                ? MediaType.APPLICATION_OCTET_STREAM_VALUE
                : meta.getContentType();
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + meta.getFileName() + "\"")
                .contentLength(meta.getSize())
                .body(resource);
    }

    @Operation(summary = "Delete an attachment")
    @DeleteMapping("/attachments/{attachmentId}")
    public ResponseEntity<Void> delete(@PathVariable Long attachmentId) {
        attachmentService.deleteAttachment(attachmentId);
        return ResponseEntity.noContent().build();
    }

    private AttachmentResponseDto toDto(TaskAttachment a) {
        return new AttachmentResponseDto(a.getId(), a.getTaskId(), a.getFileName(),
                a.getContentType(), a.getSize(), a.getUploadedAt());
    }
}
