package com.ft.messagequeueproducer;

import com.ft.messagequeueproducer.health.QueueProxyHealthcheck;
import com.ft.messagequeueproducer.model.MessageRecord;
import com.ft.messagequeueproducer.model.MessageWithRecords;
import com.ft.messaging.standards.message.v1.Message;
import com.sun.jersey.api.client.Client;

import java.nio.charset.Charset;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class QueueProxyProducer implements MessageProducer {

    private static final Charset UTF8 = Charset.forName("UTF-8");

    private final QueueProxyService queueProxyService;

    public QueueProxyProducer(final QueueProxyService queueProxyService) {
        this.queueProxyService = queueProxyService;
    }

    @Override
    public void send(final List<Message> messages) {
        final List<MessageRecord> records = messages.stream()
                .map(Message::toStringFull)
                .map(s -> new MessageRecord(s.getBytes(UTF8)))
                .collect(Collectors.toList());
        final MessageWithRecords messageWithRecords =
                new MessageWithRecords(records);
        try {
            queueProxyService.send(messageWithRecords);
        } catch (QueueProxyServiceException ex) {
            final Optional<String> concatMsgBodies = messages.stream()
                    .map(Message::getMessageBody)
                    .reduce((s, acc) -> s + "\n" + acc);
            throw new QueueProxyProducerException(String.format("Couldn't produce messages: [%s]",
                    concatMsgBodies.orElse("none.")), ex);
        }
    }

    public static JerseyClientNeeded builder() {
        return new Builder();
    }

    public static class Builder implements HttpClientNeeded, JerseyClientNeeded, ConfigurationNeeded, BuildNeeded {

        private HttpClient httpClient;
        private QueueProxyConfiguration queueProxyConfiguration;

        @Override
        public ConfigurationNeeded withHttpClient(final HttpClient httpClient) {
            this.httpClient = httpClient;
            return this;
        }

        @Override
        public ConfigurationNeeded withJerseyClient(final Client jerseyClient) {
            this.httpClient = new JerseyClient(jerseyClient);
            return this;
        }

        @Override
        public BuildNeeded withQueueProxyConfiguration(final QueueProxyConfiguration queueProxyConfiguration) {
            this.queueProxyConfiguration = queueProxyConfiguration;
            return this;
        }

        @Override
        public QueueProxyProducer build() {
            return new QueueProxyProducer(new QueueProxyServiceImpl(queueProxyConfiguration, httpClient));
        }

        @Override
        public QueueProxyHealthcheck buildHealthcheck() {
            return new QueueProxyHealthcheck(new QueueProxyServiceImpl(queueProxyConfiguration, httpClient));
        }
    }

    public interface HttpClientNeeded {
        ConfigurationNeeded withHttpClient(final HttpClient httpClient);
    }

    public interface JerseyClientNeeded extends HttpClientNeeded {
        ConfigurationNeeded withJerseyClient(final Client jerseyClient);
    }

    public interface ConfigurationNeeded {
        BuildNeeded withQueueProxyConfiguration(final QueueProxyConfiguration queueProxyConfiguration);
    }

    public interface BuildNeeded {
        QueueProxyProducer build();

        QueueProxyHealthcheck buildHealthcheck();
    }
}
