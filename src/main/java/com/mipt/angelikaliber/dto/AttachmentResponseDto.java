package com.mipt.angelikaliber.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "Metadata about an uploaded attachment")
public class AttachmentResponseDto {

    private Long id;
    private Long taskId;
    private String fileName;
    private String contentType;
    private long size;
    private LocalDateTime uploadedAt;

    public AttachmentResponseDto() {
    }

    public AttachmentResponseDto(Long id, Long taskId, String fileName,
                                 String contentType, long size, LocalDateTime uploadedAt) {
        this.id = id;
        this.taskId = taskId;
        this.fileName = fileName;
        this.contentType = contentType;
        this.size = size;
        this.uploadedAt = uploadedAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public LocalDateTime getUploadedAt() {
        return uploadedAt;
    }

    public void setUploadedAt(LocalDateTime uploadedAt) {
        this.uploadedAt = uploadedAt;
    }
}
