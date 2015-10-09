package com.ft.messagequeueproducer.health;

import com.ft.jerseyhttpwrapper.ResilientClient;
import com.ft.jerseyhttpwrapper.config.EndpointConfiguration;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import static javax.ws.rs.core.Response.Status.OK;
import static javax.ws.rs.core.Response.Status.SERVICE_UNAVAILABLE;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class QueueProxyHealthcheckTest {

    private final static Map<String, String> HEADERS = new HashMap<>();{{
        HEADERS.put("Host", "queue-proxy");
    }}

    @Test
    public void testHealthy() {
        final EndpointConfiguration mockedEndponintConfig = mock(EndpointConfiguration.class);
        final ResilientClient mockedClient = mock(ResilientClient.class);
        final QueueProxyHealthcheck healthcheck = new QueueProxyHealthcheck(mockedEndponintConfig, mockedClient, "test", HEADERS);
        final WebResource mockedResource = mock(WebResource.class);
        final WebResource.Builder mockedBuilder = mock(WebResource.Builder.class);
        when(mockedClient.resource(any(URI.class))).thenReturn(mockedResource);
        when(mockedResource.header(anyString(), anyString())).thenReturn(mockedBuilder);
        when(mockedResource.getRequestBuilder()).thenReturn(mockedBuilder);
        when(mockedBuilder.header(anyString(), anyString())).thenReturn(mockedBuilder);
        final ClientResponse mockedResponse = mock(ClientResponse.class);
        when(mockedBuilder.get(any(Class.class))).thenReturn(mockedResponse);
        when(mockedResponse.getStatus()).thenReturn(OK.getStatusCode());

        assertThat(healthcheck.check().isPresent(), is(false));
    }

    @Test
    public void testUnhealthyWhenException() {
        final EndpointConfiguration mockedEndponintConfig = mock(EndpointConfiguration.class);
        final ResilientClient mockedClient = mock(ResilientClient.class);
        final QueueProxyHealthcheck healthcheck = new QueueProxyHealthcheck(mockedEndponintConfig, mockedClient, "test", HEADERS);
        final WebResource mockedResource = mock(WebResource.class);
        final WebResource.Builder mockedBuilder = mock(WebResource.Builder.class);
        when(mockedClient.resource(any(URI.class))).thenReturn(mockedResource);
        when(mockedResource.header(anyString(), anyString())).thenReturn(mockedBuilder);
        when(mockedResource.getRequestBuilder()).thenReturn(mockedBuilder);
        when(mockedBuilder.header(anyString(), anyString())).thenReturn(mockedBuilder);
        final ClientResponse mockedResponse = mock(ClientResponse.class);
        when(mockedBuilder.get(any(Class.class))).thenThrow(new ClientHandlerException("reason"));

        assertThat(healthcheck.check().get().getThrowable().getMessage(), equalTo("reason"));
    }

    @Test
    public void testUnhealthyWhenBadStatus() {
        final EndpointConfiguration mockedEndponintConfig = mock(EndpointConfiguration.class);
        final ResilientClient mockedClient = mock(ResilientClient.class);
        final QueueProxyHealthcheck healthcheck = new QueueProxyHealthcheck(mockedEndponintConfig, mockedClient, "test", HEADERS);
        final WebResource mockedResource = mock(WebResource.class);
        final WebResource.Builder mockedBuilder = mock(WebResource.Builder.class);
        when(mockedClient.resource(any(URI.class))).thenReturn(mockedResource);
        when(mockedResource.header(anyString(), anyString())).thenReturn(mockedBuilder);
        when(mockedResource.getRequestBuilder()).thenReturn(mockedBuilder);
        when(mockedBuilder.header(anyString(), anyString())).thenReturn(mockedBuilder);
        final ClientResponse mockedResponse = mock(ClientResponse.class);
        when(mockedBuilder.get(any(Class.class))).thenReturn(mockedResponse);
        when(mockedResponse.getStatus()).thenReturn(SERVICE_UNAVAILABLE.getStatusCode());

        assertThat(healthcheck.check().get().getMessage(), equalTo("Status is 503"));
    }
}