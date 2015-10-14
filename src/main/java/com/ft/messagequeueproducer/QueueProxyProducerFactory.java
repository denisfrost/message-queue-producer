package com.ft.messagequeueproducer;

public class QueueProxyProducerFactory {

    public static QueueProxyProducer createQueueProxyProducer(final QueueProxyConfiguration queueProxyConfiguration,
            final HttpClient httpClient) {
        return new QueueProxyProducer(new QueueProxyServiceImpl(queueProxyConfiguration, httpClient));
    }
}
