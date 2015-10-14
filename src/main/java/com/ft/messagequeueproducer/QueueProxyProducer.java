package com.ft.messagequeueproducer;

import com.ft.messaging.standards.message.v1.Message;

import java.nio.charset.Charset;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class QueueProxyProducer implements MessageProducer {

    private static final Charset UTF8 = Charset.forName("UTF-8");

    private final Base64.Encoder encoder = Base64.getEncoder();
    private final QueueProxyService queueProxyService;

    public QueueProxyProducer(final QueueProxyService queueProxyService) {
        this.queueProxyService = queueProxyService;
    }

    @Override
    public void send(final List<Message> messages) {
        final List<MessageRecord> records = messages.stream()
                .map(Message::toStringFull)
                .map(s -> encoder.encode(s.getBytes(UTF8)))
                .map(MessageRecord::new)
                .collect(Collectors.toList());
        try {
            queueProxyService.send(records);
        } catch (QueueProxyServiceException ex) {
            final Optional<String> concatMsgBodies = messages.stream()
                    .map(Message::getMessageBody)
                    .reduce((s, acc) -> s + "\n" + acc);
            throw new QueueProxyProducerException(String.format("Couldn't produce messages: [%s]",
                    concatMsgBodies.orElse("none.")), ex);
        }
    }
}
