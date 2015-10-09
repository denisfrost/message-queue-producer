package com.ft.messagequeueproducer;

import com.ft.messaging.standards.message.v1.Message;

import java.util.List;

public interface MessageProducer {

    void send(final List<Message> messages);
}
