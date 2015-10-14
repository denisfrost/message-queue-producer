package com.ft.messagequeueproducer.health;

import com.ft.messagequeueproducer.HttpClient;

import java.net.URI;
import java.util.Map;
import java.util.Optional;

public class QueueProxyHealthcheck {

    private final URI uri;
    private final HttpClient httpClient;
    private final Map<String, String> additionalHeaders;

    public QueueProxyHealthcheck(final URI uri,
            final HttpClient httpClient,
            final Map<String, String> additionalHeaders) {
        this.uri = uri;
        this.httpClient = httpClient;
        this.additionalHeaders = additionalHeaders;
    }

    public Optional<Unhealthy> check() {
        try {
            final HttpClient.HttpResponse response = httpClient.get(uri, additionalHeaders);
            if (200 != response.getStatus()) {
                return Optional.of(new Unhealthy("Status is " + response.getStatus()));
            }
        } catch (final HttpClient.HttpClientException ex) {
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
