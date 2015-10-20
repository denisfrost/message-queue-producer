package com.ft.messagequeueproducer.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class MessageWithRecords {

    private final List<MessageRecord> records;

    public MessageWithRecords(@JsonProperty("records") final List<MessageRecord> records) {
        this.records = records;
    }

    public List<MessageRecord> getRecords() {
        return records;
    }
}
