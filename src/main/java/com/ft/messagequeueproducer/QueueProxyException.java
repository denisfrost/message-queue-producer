package com.ft.messagequeueproducer;

public class QueueProxyException extends RuntimeException {

    private int statusCode;

    public QueueProxyException(final String message, final Throwable cause) {
        super(message, cause);
    }
    public QueueProxyException(final int statusCode, final String message, final Throwable cause) {
        super(message, cause);
        this.statusCode = statusCode;
    }

    public QueueProxyException(final int statusCode, final String message) {
        super(message);
        this.statusCode = statusCode;
    }

    public int getStatusCode() {
        return statusCode;
    }
}
