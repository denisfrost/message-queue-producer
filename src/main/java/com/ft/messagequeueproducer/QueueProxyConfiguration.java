package com.ft.messagequeueproducer;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

public class QueueProxyConfiguration {

    private final String topicName;
    private final String queueProxyHost;
    private final Map<String, String> additionalHeaders;

    public QueueProxyConfiguration(@JsonProperty("topicName") String topicName,
            @JsonProperty("queueProxyHost") String queueProxyHost,
            @JsonProperty("additionalHeaders") Map<String, String> additionalHeaders) {
        this.topicName = topicName;
        this.queueProxyHost = queueProxyHost;
        this.additionalHeaders = additionalHeaders;
    }

    public String getTopicName() {
        return topicName;
    }

    public String getQueueProxyHost() {
        return queueProxyHost;
    }

    public Map<String, String> getAdditionalHeaders() {
        return additionalHeaders;
    }
}
