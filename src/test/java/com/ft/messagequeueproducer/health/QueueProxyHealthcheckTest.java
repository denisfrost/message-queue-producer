package com.ft.messagequeueproducer.health;

import com.ft.messagequeueproducer.QueueProxyService;
import com.ft.messagequeueproducer.QueueProxyServiceException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.doThrow;
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
        final QueueProxyService mockedService = mock(QueueProxyService.class);
        final QueueProxyHealthcheck healthcheck = new QueueProxyHealthcheck(mockedService);
        when(mockedService.doesConfiguredTopicExist()).thenReturn(true);

        assertThat(healthcheck.check().isPresent(), is(false));
    }

    @Test
    public void testUnhealthyWhenException() {
        final QueueProxyService mockedService = mock(QueueProxyService.class);
        final QueueProxyHealthcheck healthcheck = new QueueProxyHealthcheck(mockedService);
        doThrow(new QueueProxyServiceException("Request interrupted", new RuntimeException("just because")))
                .when(mockedService).doesConfiguredTopicExist();

        assertThat(healthcheck.check().get().getThrowable().getMessage(), equalTo("Request interrupted"));
    }

    @Test
    public void testUnhealthyWhenBadStatus() {
        final QueueProxyService mockedService = mock(QueueProxyService.class);
        final QueueProxyHealthcheck healthcheck = new QueueProxyHealthcheck(mockedService);
        when(mockedService.doesConfiguredTopicExist()).thenReturn(false);

        assertThat(healthcheck.check().get().getMessage(), equalTo("Topic doesn't exist."));
    }
}