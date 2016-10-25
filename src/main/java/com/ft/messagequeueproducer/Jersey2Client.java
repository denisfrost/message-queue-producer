package com.ft.messagequeueproducer;

import com.ft.messagequeueproducer.model.MessageWithRecords;

import javax.ws.rs.ProcessingException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import java.net.URI;
import java.util.Map;
import java.util.Optional;

public class Jersey2Client implements HttpClient {

    private static final String JSON = "application/json";

    private final Client queueProxyClient;

    public Jersey2Client(final Client queueProxyClient) {
        this.queueProxyClient = queueProxyClient;
    }

    @Override
    public HttpResponse get(final URI uri, final Optional<Map<String, String>> additionalHeaders) {
        Response response = null;
        try {
            final WebTarget webResource = queueProxyClient.target(uri);
            Invocation.Builder builder = webResource.request();
            if (additionalHeaders.isPresent()) {
                for (final Map.Entry<String, String> entry : additionalHeaders.get().entrySet()) {
                    builder = builder.header(entry.getKey(), entry.getValue());
                }
            }
            
            response = builder.buildGet().invoke(Response.class);
            
            return new HttpResponse(response.getStatus(), response.readEntity(String.class));
        } catch (ProcessingException ex) {
            throw new HttpClientException("Jersey client throwing exception.", ex);
        } finally {
            if (response != null) {
                response.close();
            }
        }
    }

    @Override
    public HttpResponse post(final URI uri, final MessageWithRecords messageWithRecords,
            final Optional<Map<String, String>> additionalHeaders) {
        Response response = null;
        try {
            final WebTarget webResource = queueProxyClient.target(uri);
            Invocation.Builder builder = webResource.request(JSON);
            if (additionalHeaders.isPresent()) {
                for (final Map.Entry<String, String> entry : additionalHeaders.get().entrySet()) {
                    builder = builder.header(entry.getKey(), entry.getValue());
                }
            }
            
            response = builder.buildPost(Entity.entity(messageWithRecords, JSON)).invoke(Response.class);
            return new HttpResponse(response.getStatus(), response.readEntity(String.class));
        } catch (final ProcessingException ex) {
            throw new HttpClientException("Jersey client throwing exception.", ex);
        } finally {
            if (response != null) {
                response.close();
            }
        }
    }

    @Override
    public URI buildURI(final QueueProxyConfiguration queueProxyConfiguration) {
        return UriBuilder.fromUri("http://" + queueProxyConfiguration.getProxyHostAndPort())
                .path("topics")
                .path(queueProxyConfiguration.getTopicName())
                .build();
    }
}
