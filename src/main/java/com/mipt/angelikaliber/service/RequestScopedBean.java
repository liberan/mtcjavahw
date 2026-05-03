package com.mipt.angelikaliber.service;

import java.time.Instant;
import java.util.UUID;

public class RequestScopedBean {

    private final String requestId;
    private final Instant startedAt;

    public RequestScopedBean() {
        this.requestId = UUID.randomUUID().toString();
        this.startedAt = Instant.now();
    }

    public String getRequestId() {
        return requestId;
    }

    public Instant getStartedAt() {
        return startedAt;
    }
}
