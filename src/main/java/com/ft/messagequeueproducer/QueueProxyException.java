package com.ft.messagequeueproducer;

public class QueueProxyException extends RuntimeException {
    private final int statusCode;

    public QueueProxyException(int statusCode, String errorMessage) {
        super(errorMessage);
        this.statusCode = statusCode;
    }

    public int getStatusCode() {
        return statusCode;
    }
}
