package com.ft.messagequeueproducer;

import com.ft.jerseyhttpwrapper.ResilientClient;
import com.ft.jerseyhttpwrapper.config.EndpointConfiguration;
import com.ft.messaging.standards.message.v1.Message;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.UriBuilder;
import java.net.URI;
import java.nio.charset.Charset;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static javax.ws.rs.core.Response.Status.OK;
import static javax.ws.rs.core.Response.Status.SERVICE_UNAVAILABLE;

public class QueueProxyProducer implements MessageProducer {

    private static final Logger LOGGER = LoggerFactory.getLogger(QueueProxyProducer.class);
    private static final String TYPE_BINARY_EMBEDDED_JSON = "application/vnd.kafka.binary.v1+json";
    private static final Charset UTF8 = Charset.forName("UTF-8");

    private final Base64.Encoder encoder = Base64.getEncoder();
    private final EndpointConfiguration queueProxyEndpointConfiguration;
    private final ResilientClient queueProxyClient;
    private final String topic;
    private final Map<String, String> additionalHeaders;

    public QueueProxyProducer(final EndpointConfiguration queueProxyEndpointConfiguration,
            final ResilientClient queueProxyClient,
            final String topic,
            final Map<String, String> additionalHeaders) {
        this.queueProxyEndpointConfiguration = queueProxyEndpointConfiguration;
        this.queueProxyClient = queueProxyClient;
        this.topic = topic;
        this.additionalHeaders = additionalHeaders;
    }

    @Override
    public void send(final List<Message> messages) {
        final URI uri = UriBuilder.fromPath(queueProxyEndpointConfiguration.getPath())
                .scheme("http")
                .host(queueProxyEndpointConfiguration.getHost())
                .port(queueProxyEndpointConfiguration.getPort())
                .build(topic);
        final List<MessageRecord> records = messages.stream()
                .map(Message::toStringFull)
                .map(s -> encoder.encode(s.getBytes(UTF8)))
                .map(MessageRecord::new)
                .collect(Collectors.toList());
        try {
            final WebResource webResource = queueProxyClient.resource(uri);
            WebResource.Builder builder = webResource.type(TYPE_BINARY_EMBEDDED_JSON);
            for (final Map.Entry<String, String> entry : additionalHeaders.entrySet()) {
                builder = builder.header(entry.getKey(), entry.getValue());
            }
            final ClientResponse clientResponse = builder.post(ClientResponse.class, records);
            handleNonOkStatus(clientResponse);
        } catch (final ClientHandlerException ex) {
            final Optional<String> concatMsgBodies = messages.stream()
                    .map(Message::getMessageBody)
                    .reduce((s, acc) -> s + "\n" + acc);
            throw new QueueProxyUnreachableException(
                    String.format("Exception during calling Queue Proxy for [%s]", concatMsgBodies.orElse("none.")), ex);
        }
    }

    private void handleNonOkStatus(final ClientResponse clientResponse) {
        int statusCode = clientResponse.getStatus();
        if (OK.getStatusCode() != statusCode) {
            String clientResponseErrorMessage = "No error message from Queue Proxy.";
            try {
                clientResponseErrorMessage = clientResponse.getEntity(String.class);
            } catch (ClientHandlerException | UniformInterfaceException e) {
                LOGGER.warn("Failed to parse Queue Proxy client response error message.", e);
            }
            if (SERVICE_UNAVAILABLE.getStatusCode() == statusCode) {
                throw new QueueProxyUnavailableException(clientResponseErrorMessage);
            }
            throw new QueueProxyException(statusCode, clientResponseErrorMessage);
        }
    }
}
