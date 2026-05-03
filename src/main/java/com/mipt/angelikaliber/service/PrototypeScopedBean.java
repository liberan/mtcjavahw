package com.mipt.angelikaliber.service;

import java.util.UUID;

public class PrototypeScopedBean {

    private final String taskId;

    public PrototypeScopedBean() {
        this.taskId = UUID.randomUUID().toString();
    }

    public String getTaskId() {
        return taskId;
    }

    public String nextTaskId() {
        return UUID.randomUUID().toString();
    }
}
