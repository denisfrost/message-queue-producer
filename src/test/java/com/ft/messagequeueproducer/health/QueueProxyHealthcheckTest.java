package com.ft.messagequeueproducer.health;

import com.ft.jerseyhttpwrapper.config.EndpointConfiguration;
import com.ft.messagequeueproducer.HttpClient;
import com.google.common.base.Optional;
import com.sun.jersey.api.client.ClientHandlerException;
import io.dropwizard.client.JerseyClientConfiguration;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.net.URI;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static javax.ws.rs.core.Response.Status.BAD_REQUEST;
import static javax.ws.rs.core.Response.Status.OK;
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
        final EndpointConfiguration endpoitConfig = new EndpointConfiguration(Optional.<String>absent(),
                Optional.of(new JerseyClientConfiguration()),
                Optional.of("/test"),
                Arrays.asList("localhost:8080"),
                Arrays.asList("localhost:9080")
        );
        final HttpClient mockedClient = mock(HttpClient.class);
        final QueueProxyHealthcheck healthcheck = new QueueProxyHealthcheck(endpoitConfig, mockedClient, "test", HEADERS);
        final HttpClient.HttpResponse response = new HttpClient.HttpResponse(OK.getStatusCode(), "");
        when(mockedClient.get(URI.create("http://localhost:8080/topics"), HEADERS)).thenReturn(response);

        assertThat(healthcheck.check().isPresent(), is(false));
    }

    @Test
    public void testUnhealthyWhenException() {
        final EndpointConfiguration endpoitConfig = new EndpointConfiguration(Optional.<String>absent(),
                Optional.of(new JerseyClientConfiguration()),
                Optional.of("/test"),
                Arrays.asList("localhost:8080"),
                Arrays.asList("localhost:9080")
        );
        final HttpClient mockedClient = mock(HttpClient.class);
        final QueueProxyHealthcheck healthcheck = new QueueProxyHealthcheck(endpoitConfig, mockedClient, "test", HEADERS);
        when(mockedClient.get(URI.create("http://localhost:8080/topics"), HEADERS))
                .thenThrow(new HttpClient.HttpClientException("can't request", new ClientHandlerException("just because")));

        assertThat(healthcheck.check().get().getThrowable().getMessage(), equalTo("can't request"));
    }

    @Test
    public void testUnhealthyWhenBadStatus() {
        final EndpointConfiguration endpoitConfig = new EndpointConfiguration(Optional.<String>absent(),
                Optional.of(new JerseyClientConfiguration()),
                Optional.of("/test"),
                Arrays.asList("localhost:8080"),
                Arrays.asList("localhost:9080")
        );
        final HttpClient mockedClient = mock(HttpClient.class);
        final QueueProxyHealthcheck healthcheck = new QueueProxyHealthcheck(endpoitConfig, mockedClient, "test", HEADERS);
        final HttpClient.HttpResponse response = new HttpClient.HttpResponse(BAD_REQUEST.getStatusCode(), "");
        when(mockedClient.get(URI.create("http://localhost:8080/topics"), HEADERS)).thenReturn(response);

        assertThat(healthcheck.check().get().getMessage(), equalTo("Status is 400"));
    }
}