package com.ft.messagequeueproducer.health;

import com.ft.messagequeueproducer.HttpClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class QueueProxyHealthcheckTest {

    private final static Map<String, String> HEADERS = new HashMap<>();
    static {{
        HEADERS.put("Host", "queue-proxy");
    }}

    @Test
    public void testHealthy() {
        final HttpClient mockedClient = mock(HttpClient.class);
        final URI uri = URI.create("http://localhost:8080/topics/test");
        final QueueProxyHealthcheck healthcheck = new QueueProxyHealthcheck(uri, mockedClient, HEADERS);
        final HttpClient.HttpResponse response = new HttpClient.HttpResponse(200, "");
        when(mockedClient.get(uri, HEADERS)).thenReturn(response);

        assertThat(healthcheck.check().isPresent(), is(false));
    }

    @Test
    public void testUnhealthyWhenException() {
        final HttpClient mockedClient = mock(HttpClient.class);
        final URI uri = URI.create("http://localhost:8080/topics/test");
        final QueueProxyHealthcheck healthcheck = new QueueProxyHealthcheck(uri, mockedClient, HEADERS);
        when(mockedClient.get(uri, HEADERS))
                .thenThrow(new HttpClient.HttpClientException("can't request", new RuntimeException("testing")));

        assertThat(healthcheck.check().get().getThrowable().getMessage(), equalTo("can't request"));
    }

    @Test
    public void testUnhealthyWhenBadStatus() {
        final HttpClient mockedClient = mock(HttpClient.class);
        final URI uri = URI.create("http://localhost:8080/topics/test");
        final QueueProxyHealthcheck healthcheck = new QueueProxyHealthcheck(uri, mockedClient, HEADERS);
        final HttpClient.HttpResponse response = new HttpClient.HttpResponse(400, "");
        when(mockedClient.get(uri, HEADERS)).thenReturn(response);

        assertThat(healthcheck.check().get().getMessage(), equalTo("Status is 400"));
    }
}