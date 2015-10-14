package com.ft.messagequeueproducer.health;

import com.ft.messagequeueproducer.QueueProxyProducerException;
import com.ft.messagequeueproducer.QueueProxyService;
import com.ft.messagequeueproducer.QueueProxyServiceException;

import java.util.Optional;

public class QueueProxyHealthcheck {

    private final QueueProxyService queueProxyService;

    public QueueProxyHealthcheck(final QueueProxyService queueProxyService) {
        this.queueProxyService = queueProxyService;
    }

    public Optional<Unhealthy> check() {

        try {
            if (!queueProxyService.doesConfiguredTopicExist()){
                return Optional.of(new Unhealthy("Topic doesn't exist."));
            }
        } catch (final QueueProxyServiceException ex) {
            return Optional.of(new Unhealthy(ex));
        }
        return Optional.empty();
    }

    public class Unhealthy {

        private String message;
        private Throwable throwable;

        public Unhealthy(final String message) {
            this.message = message;
        }

        public Unhealthy(final Throwable throwable) {
            this.throwable = throwable;
        }

        public String getMessage() {
            return message;
        }

        public Throwable getThrowable() {
            return throwable;
        }
    }
}
