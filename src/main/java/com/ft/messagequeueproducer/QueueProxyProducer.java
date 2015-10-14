package com.ft.messagequeueproducer;

import com.ft.messaging.standards.message.v1.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.nio.charset.Charset;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class QueueProxyProducer implements MessageProducer {

    public static final String TYPE_BINARY_EMBEDDED_JSON = "application/vnd.kafka.binary.v1+json";
    private static final Logger LOGGER = LoggerFactory.getLogger(QueueProxyProducer.class);
    private static final Charset UTF8 = Charset.forName("UTF-8");

    private final Base64.Encoder encoder = Base64.getEncoder();
    private final URI uri;
    private final Map<String, String> additionalHeaders;
    private final HttpClient httpClient;

    public QueueProxyProducer(final URI uri,
            final Map<String, String> additionalHeaders,
            final HttpClient httpClient) {
        this.uri = uri;
        this.additionalHeaders = additionalHeaders;
        this.httpClient = httpClient;
    }

    @Override
    public void send(final List<Message> messages) {
        final List<MessageRecord> records = messages.stream()
                .map(Message::toStringFull)
                .map(s -> encoder.encode(s.getBytes(UTF8)))
                .map(MessageRecord::new)
                .collect(Collectors.toList());

        HttpClient.HttpResponse response;
        try {
            response = httpClient.post(uri, records, TYPE_BINARY_EMBEDDED_JSON, additionalHeaders);
        } catch (HttpClient.HttpClientException ex) {
            final Optional<String> concatMsgBodies = messages.stream()
                    .map(Message::getMessageBody)
                    .reduce((s, acc) -> s + "\n" + acc);
            throw new QueueProxyUnreachableException(
                    String.format("Exception during calling Queue Proxy for [%s]", concatMsgBodies.orElse("none.")), ex);
        }
        handleNonOkStatus(response);
    }

    private void handleNonOkStatus(final HttpClient.HttpResponse response) {
        int statusCode = response.getStatus();
        if (200 != statusCode) {
            String clientResponseErrorMessage = "No error message from Queue Proxy.";
            try {
                clientResponseErrorMessage = response.getBody();
            } catch (HttpClient.HttpClientException e) {
                LOGGER.warn("Failed to parse Queue Proxy client response error message.", e);
            }
            if (503 == statusCode) {
                throw new QueueProxyUnavailableException(clientResponseErrorMessage);
            }
            throw new QueueProxyException(statusCode, clientResponseErrorMessage);
        }
    }
}
