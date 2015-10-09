package com.ft.messagequeueproducer.health;

import com.ft.jerseyhttpwrapper.ResilientClient;
import com.ft.jerseyhttpwrapper.config.EndpointConfiguration;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

import javax.ws.rs.core.UriBuilder;
import java.net.URI;
import java.util.Map;
import java.util.Optional;

import static javax.ws.rs.core.Response.Status.OK;

public class QueueProxyHealthcheck {

    private final EndpointConfiguration queueProxyEndpointConfiguration;
    private final ResilientClient queueProxyClient;
    private final String topic;
    private final Map<String, String> additionalHeaders;

    public QueueProxyHealthcheck(final EndpointConfiguration queueProxyEndpointConfiguration,
            final ResilientClient queueProxyClient,
            final String topic,
            final Map<String, String> additionalHeaders) {
        this.queueProxyEndpointConfiguration = queueProxyEndpointConfiguration;
        this.queueProxyClient = queueProxyClient;
        this.topic = topic;
        this.additionalHeaders = additionalHeaders;
    }

    public Optional<Unhealthy> check() {
        final URI uri = UriBuilder.fromPath("topics")
                .scheme("http")
                .host(queueProxyEndpointConfiguration.getHost())
                .port(queueProxyEndpointConfiguration.getAdminPort())
                .build(topic);
        ClientResponse clientResponse = null;
        try {
            final WebResource webResource = queueProxyClient.resource(uri);
            WebResource.Builder builder = webResource.getRequestBuilder();
            for (final Map.Entry<String, String> entry : additionalHeaders.entrySet()) {
                builder = builder.header(entry.getKey(), entry.getValue());
            }
            clientResponse = builder.get(ClientResponse.class);
            int statusCode = clientResponse.getStatus();
            if (OK.getStatusCode() != statusCode) {
                return Optional.of(new Unhealthy("Status is " + statusCode));
            }
        } catch (final ClientHandlerException ex) {
            return Optional.of(new Unhealthy(ex));
        } finally {
            if (clientResponse != null) {
                clientResponse.close();
            }
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
