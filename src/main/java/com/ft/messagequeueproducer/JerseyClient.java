package com.ft.messagequeueproducer;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

import java.net.URI;
import java.util.List;
import java.util.Map;

public class JerseyClient implements HttpClient {

    private final Client queueProxyClient;

    public JerseyClient(final Client queueProxyClient) {
        this.queueProxyClient = queueProxyClient;
    }

    @Override
    public HttpResponse get(final URI uri, final Map<String, String> additionalHeaders) {
        ClientResponse clientResponse = null;
        try {
            final WebResource webResource = queueProxyClient.resource(uri);
            WebResource.Builder builder = webResource.getRequestBuilder();
            for (final Map.Entry<String, String> entry : additionalHeaders.entrySet()) {
                builder = builder.header(entry.getKey(), entry.getValue());
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
    public HttpResponse post(final URI uri, final List<MessageRecord> messageRecords, final String contentType,
            final Map<String, String> additionalHeaders) {
        ClientResponse clientResponse = null;
        try {
            final WebResource webResource = queueProxyClient.resource(uri);
            WebResource.Builder builder = webResource.type(contentType);
            for (final Map.Entry<String, String> entry : additionalHeaders.entrySet()) {
                builder = builder.header(entry.getKey(), entry.getValue());
            }
            clientResponse = builder.post(ClientResponse.class, messageRecords);
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
        return queueProxyClient
                .resource("topics")
                .path(queueProxyConfiguration.getTopicName())
                .getUriBuilder()
                .build();
    }
}
