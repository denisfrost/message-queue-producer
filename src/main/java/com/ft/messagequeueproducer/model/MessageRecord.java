package com.ft.messagequeueproducer.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonIgnoreProperties(ignoreUnknown = true)
public class MessageRecord {
    private final byte[] key;
    private final byte[] value;

    public MessageRecord(@JsonProperty("key") byte[] key, @JsonProperty("value") byte[] value) {
        this.key = key;
        this.value = value;
    }
    
    public byte[] getKey() {
        return key;
    }
    
    public byte[] getValue() {
        return value;
    }
}
