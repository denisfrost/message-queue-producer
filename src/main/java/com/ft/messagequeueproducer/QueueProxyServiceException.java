package com.ft.messagequeueproducer;

public class QueueProxyServiceException extends RuntimeException {

    private int statusCode;

    public QueueProxyServiceException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public QueueProxyServiceException(final int statusCode, final String message, final Throwable cause) {
        super(message, cause);
        this.statusCode = statusCode;
    }

    public QueueProxyServiceException(final int statusCode, final String message) {
        super(message);
        this.statusCode = statusCode;
    }
}
