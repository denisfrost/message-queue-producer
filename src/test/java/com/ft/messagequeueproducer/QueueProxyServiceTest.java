package com.ft.messagequeueproducer;

import com.ft.messagequeueproducer.model.MessageWithRecords;
import com.ft.messagequeueproducer.model.MessageRecord;
import com.sun.jersey.api.client.ClientHandlerException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static javax.ws.rs.core.Response.Status.BAD_REQUEST;
import static javax.ws.rs.core.Response.Status.OK;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class QueueProxyServiceTest {

    private final static Map<String, String> HEADERS = new HashMap<>();
    static {{
            HEADERS.put("Host", "queue-proxy");
    }}
    private final static List<MessageRecord> RECORDS = new ArrayList<>();
    static {{
        for (byte i = 0; i < 2; i++) {
            byte[] arr = new byte[1];
            arr[0] = i;
            final MessageRecord record = new MessageRecord(null, arr);
            RECORDS.add(record);
        }
    }}
    private final static MessageWithRecords MESSAGE_WITH_RECORDS = new MessageWithRecords(RECORDS);

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void testHappySending() {
        final QueueProxyConfiguration queueProxyConfiguration = new QueueProxyConfiguration("test", "http://localhost:8080", HEADERS);
        final HttpClient mockedHttpClient = mock(HttpClient.class);
        final QueueProxyService queueProxyService = new QueueProxyServiceImpl(queueProxyConfiguration, mockedHttpClient);
        final URI uri = URI.create("http://localhost:8080/topics/test");
        when(mockedHttpClient.buildURI(queueProxyConfiguration)).thenReturn(uri);
        when(mockedHttpClient.post(eq(uri),
                any(MessageWithRecords.class),
                eq(Optional.of(HEADERS))))
                .thenReturn(new HttpClient.HttpResponse(OK.getStatusCode(), ""));

        queueProxyService.send(MESSAGE_WITH_RECORDS);
    }

    @Test
    public void testExceptionIfBadStatus() {
        final QueueProxyConfiguration queueProxyConfiguration = new QueueProxyConfiguration("test", "http://localhost:8080", HEADERS);
        final HttpClient mockedHttpClient = mock(HttpClient.class);
        final QueueProxyService queueProxyService = new QueueProxyServiceImpl(queueProxyConfiguration, mockedHttpClient);
        final URI uri = URI.create("http://localhost:8080/topics/test");
        when(mockedHttpClient.buildURI(queueProxyConfiguration)).thenReturn(uri);
        when(mockedHttpClient.post(eq(uri),
                any(MessageWithRecords.class),
                eq(Optional.of(HEADERS))))
                .thenReturn(new HttpClient.HttpResponse(BAD_REQUEST.getStatusCode(), ""));
        thrown.expect(QueueProxyServiceException.class);

        queueProxyService.send(MESSAGE_WITH_RECORDS);
    }

    @Test
    public void testExceptionIfHttpExceptionStatus() {
        final QueueProxyConfiguration queueProxyConfiguration = new QueueProxyConfiguration("test", "http://localhost:8080", HEADERS);
        final HttpClient mockedHttpClient = mock(HttpClient.class);
        final QueueProxyService queueProxyService = new QueueProxyServiceImpl(queueProxyConfiguration, mockedHttpClient);
        final URI uri = URI.create("http://localhost:8080/topics/test");
        when(mockedHttpClient.buildURI(queueProxyConfiguration)).thenReturn(uri);
        when(mockedHttpClient.post(eq(uri),
                any(MessageWithRecords.class),
                eq(Optional.of(HEADERS))))
                .thenThrow(new HttpClient.HttpClientException("couldn't request", new ClientHandlerException("no")));
        thrown.expect(QueueProxyServiceException.class);

        queueProxyService.send(MESSAGE_WITH_RECORDS);
    }
}