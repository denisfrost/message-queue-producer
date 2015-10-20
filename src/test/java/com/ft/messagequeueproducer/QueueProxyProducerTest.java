package com.ft.messagequeueproducer;

import com.ft.messagequeueproducer.model.MessageWithRecords;
import com.ft.messaging.standards.message.v1.Message;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class QueueProxyProducerTest {

    private final static List<Message> MESSAGES = new ArrayList<>();
    static {{
        for (int i = 0; i < 2; i++) {
            final Message message = Message.message()
                    .withMessageId(UUID.randomUUID())
                    .withMessageType("type")
                    .withMessageTimestamp(new Date(System.currentTimeMillis()))
                    .withOriginSystemId("test")
                    .withContentType("json")
                    .withMessageBody(Integer.toString(i)).build();
            MESSAGES.add(message);
        }
    }}

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void testHappyProducing() {
        final QueueProxyService mockedService = mock(QueueProxyService.class);
        final QueueProxyProducer producer = new QueueProxyProducer(mockedService);

        producer.send(MESSAGES);
        verify(mockedService).send(any(MessageWithRecords.class));
    }

    @Test
    public void testExceptionIfHttpExceptionStatus() {
        final QueueProxyService mockedService = mock(QueueProxyService.class);
        final QueueProxyProducer producer = new QueueProxyProducer(mockedService);
        doThrow(new QueueProxyServiceException("couldn't request", new RuntimeException("no")))
                .when(mockedService).send(any(MessageWithRecords.class));
        thrown.expect(QueueProxyProducerException.class);

        producer.send(MESSAGES);
    }
}
