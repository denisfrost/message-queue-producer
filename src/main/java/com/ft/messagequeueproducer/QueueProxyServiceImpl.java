package com.ft.messagequeueproducer;

import com.ft.messagequeueproducer.model.MessageWithRecords;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;

import static javax.ws.rs.core.Response.Status.NOT_FOUND;
import static javax.ws.rs.core.Response.Status.OK;

public class QueueProxyServiceImpl implements QueueProxyService {

    public static final String TYPE_BINARY_EMBEDDED_JSON = "application/vnd.kafka.binary.v1+json";
    private static final Logger LOGGER = LoggerFactory.getLogger(QueueProxyServiceImpl.class);

    private final QueueProxyConfiguration queueProxyConfiguration;
    private final HttpClient httpClient;

    public QueueProxyServiceImpl(final QueueProxyConfiguration queueProxyConfiguration,
            final HttpClient httpClient) {
        this.httpClient = httpClient;
        this.queueProxyConfiguration = queueProxyConfiguration;
    }

    @Override
    public void send(final MessageWithRecords messageWithRecords) {
        HttpClient.HttpResponse response;
        final URI uri = httpClient.buildURI(queueProxyConfiguration);
        try {
            response = httpClient.post(uri, messageWithRecords, queueProxyConfiguration.getAdditionalHeaders());
        } catch (final HttpClient.HttpClientException ex) {
            throw new QueueProxyServiceException("Couldn't send records.", ex);
        }
        handleNonOkStatus(response);
    }

    @Override
    public boolean doesConfiguredTopicExist() {
        final URI uri = httpClient.buildURI(queueProxyConfiguration);
        try {
            final HttpClient.HttpResponse response = httpClient.get(uri, queueProxyConfiguration.getAdditionalHeaders());
            if (NOT_FOUND.getStatusCode() == response.getStatus()) {
                return false;
            } else if (OK.getStatusCode() != response.getStatus()) {
                throw new QueueProxyServiceException(response.getStatus(), "Queue proxy responded with not expected status.");
            }
        } catch (final HttpClient.HttpClientException ex) {
            throw new QueueProxyServiceException("Couldn't check if topic exists.", ex);
        }
        return true;
    }

    private void handleNonOkStatus(final HttpClient.HttpResponse response) {
        final int statusCode = response.getStatus();
        if (OK.getStatusCode() != statusCode) {
            String clientResponseErrorMessage = "Unable to obtain body of non-OK response from Queue Proxy.";
            try {
                clientResponseErrorMessage = "Non-OK response was received: " + response.getBody();
            } catch (HttpClient.HttpClientException e) {
                LOGGER.warn("Failed to parse Queue Proxy client response error message.", e);
                throw new QueueProxyServiceException(statusCode, clientResponseErrorMessage, e);
            }
            throw new QueueProxyServiceException(statusCode, clientResponseErrorMessage);
        }
    }
}
