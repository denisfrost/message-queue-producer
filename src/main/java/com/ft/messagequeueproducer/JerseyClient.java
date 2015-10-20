package com.ft.messagequeueproducer;

import com.ft.messagequeueproducer.model.MessageWithRecords;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

import javax.ws.rs.core.UriBuilder;
import java.net.URI;
import java.util.Map;
import java.util.Optional;

public class JerseyClient implements HttpClient {

    private static final String JSON = "application/json";

    private final Client queueProxyClient;

    public JerseyClient(final Client queueProxyClient) {
        this.queueProxyClient = queueProxyClient;
    }

    @Override
    public HttpResponse get(final URI uri, final Optional<Map<String, String>> additionalHeaders) {
        ClientResponse clientResponse = null;
        try {
            final WebResource webResource = queueProxyClient.resource(uri);
            WebResource.Builder builder = webResource.getRequestBuilder();
            if (additionalHeaders.isPresent()) {
                for (final Map.Entry<String, String> entry : additionalHeaders.get().entrySet()) {
                    builder = builder.header(entry.getKey(), entry.getValue());
                }
            }
            clientResponse = builder.get(ClientResponse.class);
            return new HttpResponse(clientResponse.getStatus(), clientResponse.getEntity(String.class));
        } catch (ClientHandlerException ex) {
            throw new HttpClientException("Jersey client throwing exception.", ex);
        } finally {
            if (clientResponse != null) {
                clientResponse.close();
            }
        }
    }

    @Override
    public HttpResponse post(final URI uri, final MessageWithRecords messageWithRecords,
            final Optional<Map<String, String>> additionalHeaders) {
        ClientResponse clientResponse = null;
        try {
            final WebResource webResource = queueProxyClient.resource(uri);
            WebResource.Builder builder = webResource.type(JSON);
            if (additionalHeaders.isPresent()) {
                for (final Map.Entry<String, String> entry : additionalHeaders.get().entrySet()) {
                    builder = builder.header(entry.getKey(), entry.getValue());
                }
            }
            clientResponse = builder.post(ClientResponse.class, messageWithRecords);
            return new HttpResponse(clientResponse.getStatus(), clientResponse.getEntity(String.class));
        } catch (final ClientHandlerException ex) {
            throw new HttpClientException("Jersey client throwing exception.", ex);
        } finally {
            if (clientResponse != null) {
                clientResponse.close();
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
