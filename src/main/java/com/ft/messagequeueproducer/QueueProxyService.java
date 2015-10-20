package com.ft.messagequeueproducer;

import com.ft.messagequeueproducer.model.MessageWithRecords;

public interface QueueProxyService {

    void send(final MessageWithRecords messageWithRecords);

    boolean doesConfiguredTopicExist();
}
