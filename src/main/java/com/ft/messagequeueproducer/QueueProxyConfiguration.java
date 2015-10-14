package com.ft.messagequeueproducer;

import com.fasterxml.jackson.annotation.JsonProperty;

public class QueueProxyConfiguration {

    private final String topicName;
    private final String queueProxyHost;

    public QueueProxyConfiguration(@JsonProperty("topicName") String topicName,
            @JsonProperty("queueProxyHost") String queueProxyHost) {
        this.topicName = topicName;
        this.queueProxyHost = queueProxyHost;
    }

    public String getTopicName() {
        return topicName;
    }

    public String getQueueProxyHost() {
        return queueProxyHost;
    }
}
