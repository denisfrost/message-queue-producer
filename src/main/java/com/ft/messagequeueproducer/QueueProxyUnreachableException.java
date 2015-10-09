package com.ft.messagequeueproducer;

public class QueueProxyUnreachableException extends RuntimeException {

    public QueueProxyUnreachableException(String message, Throwable cause) {
        super(message, cause);
    }
}
