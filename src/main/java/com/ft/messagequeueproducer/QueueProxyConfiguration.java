package com.ft.messagequeueproducer;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;
import java.util.Optional;

public class QueueProxyConfiguration {

    private final String topicName;
    private final String proxyHostAndPort;
    private final Map<String, String> additionalHeaders;

    public QueueProxyConfiguration(@JsonProperty("topicName") String topicName,
            @JsonProperty("proxyHostAndPort") String proxyHostAndPort,
            @JsonProperty("additionalHeaders") Map<String, String> additionalHeaders) {
        this.topicName = topicName;
        this.proxyHostAndPort = proxyHostAndPort;
        this.additionalHeaders = additionalHeaders;
    }

    public String getTopicName() {
        return topicName;
    }

    public String getProxyHostAndPort() {
        return proxyHostAndPort;
    }

    public Optional<Map<String, String>> getAdditionalHeaders() {
        return Optional.ofNullable(additionalHeaders);
    }
}
