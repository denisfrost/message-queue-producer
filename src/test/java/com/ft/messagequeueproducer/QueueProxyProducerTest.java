package com.ft.messagequeueproducer;

import com.ft.jerseyhttpwrapper.config.EndpointConfiguration;
import com.ft.messaging.standards.message.v1.Message;
import com.google.common.base.Optional;
import com.sun.jersey.api.client.ClientHandlerException;
import io.dropwizard.client.JerseyClientConfiguration;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static javax.ws.rs.core.Response.Status.BAD_REQUEST;
import static javax.ws.rs.core.Response.Status.OK;
import static org.mockito.Matchers.anyList;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class QueueProxyProducerTest {

    private final static Map<String, String> HEADERS = new HashMap<>();
    static {{
        HEADERS.put("Host", "queue-proxy");
    }}

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void testHappyProducing() {
        final EndpointConfiguration endpoitConfig = new EndpointConfiguration(Optional.<String>absent(),
                Optional.of(new JerseyClientConfiguration()),
                Optional.of("/test"),
                Arrays.asList("localhost:8080"),
                Arrays.asList("localhost:9080")
        );
        final HttpClient mockedClient = mock(HttpClient.class);
        final QueueProxyProducer producer = new QueueProxyProducer(endpoitConfig, "test", HEADERS, mockedClient);
        final List<Message> messages = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            final Message message = Message.message()
                    .withMessageId(UUID.randomUUID())
                    .withMessageType("type")
                    .withMessageTimestamp(new Date(System.currentTimeMillis()))
                    .withOriginSystemId("test")
                    .withContentType("json")
                    .withMessageBody(Integer.toString(i)).build();
            messages.add(message);
        }
        when(mockedClient.post(eq(URI.create("http://localhost:8080/test")),
                anyList(),
                eq(QueueProxyProducer.TYPE_BINARY_EMBEDDED_JSON),
                eq(HEADERS)))
                .thenReturn(new HttpClient.HttpResponse(OK.getStatusCode(), ""));

        producer.send(messages);
    }

    @Test
    public void testExceptionIfBadStatus() {
        final EndpointConfiguration endpoitConfig = new EndpointConfiguration(Optional.<String>absent(),
                Optional.of(new JerseyClientConfiguration()),
                Optional.of("/test"),
                Arrays.asList("localhost:8080"),
                Arrays.asList("localhost:9080")
        );
        final HttpClient mockedClient = mock(HttpClient.class);
        final QueueProxyProducer producer = new QueueProxyProducer(endpoitConfig, "test", HEADERS, mockedClient);
        final List<Message> messages = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            final Message message = Message.message()
                    .withMessageId(UUID.randomUUID())
                    .withMessageType("type")
                    .withMessageTimestamp(new Date(System.currentTimeMillis()))
                    .withOriginSystemId("test")
                    .withContentType("json")
                    .withMessageBody(Integer.toString(i)).build();
            messages.add(message);
        }
        when(mockedClient.post(eq(URI.create("http://localhost:8080/test")),
                anyList(),
                eq(QueueProxyProducer.TYPE_BINARY_EMBEDDED_JSON),
                eq(HEADERS)))
                .thenReturn(new HttpClient.HttpResponse(BAD_REQUEST.getStatusCode(), ""));
        thrown.expect(QueueProxyException.class);

        producer.send(messages);
    }

    @Test
    public void testExceptionIfHttpExceptionStatus() {
        final EndpointConfiguration endpoitConfig = new EndpointConfiguration(Optional.<String>absent(),
                Optional.of(new JerseyClientConfiguration()),
                Optional.of("/test"),
                Arrays.asList("localhost:8080"),
                Arrays.asList("localhost:9080")
        );
        final HttpClient mockedClient = mock(HttpClient.class);
        final QueueProxyProducer producer = new QueueProxyProducer(endpoitConfig, "test", HEADERS, mockedClient);
        final List<Message> messages = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            final Message message = Message.message()
                    .withMessageId(UUID.randomUUID())
                    .withMessageType("type")
                    .withMessageTimestamp(new Date(System.currentTimeMillis()))
                    .withOriginSystemId("test")
                    .withContentType("json")
                    .withMessageBody(Integer.toString(i)).build();
            messages.add(message);
        }
        when(mockedClient.post(eq(URI.create("http://localhost:8080/test")),
                anyList(),
                eq(QueueProxyProducer.TYPE_BINARY_EMBEDDED_JSON),
                eq(HEADERS)))
                .thenThrow(new HttpClient.HttpClientException("couldn't request", new ClientHandlerException("no")));
        thrown.expect(QueueProxyUnreachableException.class);

        producer.send(messages);
    }
}
