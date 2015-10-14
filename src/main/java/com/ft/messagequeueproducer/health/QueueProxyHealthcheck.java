package com.ft.messagequeueproducer.health;

import com.ft.jerseyhttpwrapper.config.EndpointConfiguration;
import com.ft.messagequeueproducer.HttpClient;

import javax.ws.rs.core.UriBuilder;
import java.net.URI;
import java.util.Map;
import java.util.Optional;

import static javax.ws.rs.core.Response.Status.OK;

public class QueueProxyHealthcheck {

    private final EndpointConfiguration queueProxyEndpointConfiguration;
    private final HttpClient httpClient;
    private final String topic;
    private final Map<String, String> additionalHeaders;

    public QueueProxyHealthcheck(final EndpointConfiguration queueProxyEndpointConfiguration,
            final HttpClient httpClient,
            final String topic,
            final Map<String, String> additionalHeaders) {
        this.queueProxyEndpointConfiguration = queueProxyEndpointConfiguration;
        this.httpClient = httpClient;
        this.topic = topic;
        this.additionalHeaders = additionalHeaders;
    }

    public Optional<Unhealthy> check() {
        final URI uri = UriBuilder.fromPath("topics")
                .scheme("http")
                .host(queueProxyEndpointConfiguration.getHost())
                .port(queueProxyEndpointConfiguration.getAdminPort())
                .build(topic);
        try {
            final HttpClient.HttpResponse response = httpClient.get(uri, additionalHeaders);
            if (OK.getStatusCode() != response.getStatus()) {
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
