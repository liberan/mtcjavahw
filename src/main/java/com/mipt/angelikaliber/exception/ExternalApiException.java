package com.mipt.angelikaliber.exception;

public class ExternalApiException extends RuntimeException {

    private final int status;

    public ExternalApiException(int status, String message) {
        super(message);
        this.status = status;
    }

    public ExternalApiException(int status, String message, Throwable cause) {
        super(message, cause);
        this.status = status;
    }

    public int getStatus() {
        return status;
    }
}
