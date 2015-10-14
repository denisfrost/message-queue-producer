package com.ft.messagequeueproducer;

import java.util.List;

public interface QueueProxyService {

    void send(final List<MessageRecord> records);

    boolean doesConfiguredTopicExist();
}
