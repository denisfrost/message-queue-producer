package com.ft.messagequeueproducer;

import com.ft.messagequeueproducer.model.KeyedMessage;
import com.ft.messagequeueproducer.model.MessageRecord;
import com.ft.messagequeueproducer.model.MessageWithRecords;
import com.ft.messaging.standards.message.v1.Message;
import com.ft.messaging.standards.message.v1.MessageParser;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.anyOf;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;

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
    @Mock
    private QueueProxyService mockedService;
    @InjectMocks
    private QueueProxyProducer producer;

    @Test
    public void testHappyProducing() {
        producer.send(MESSAGES);
        
        ArgumentCaptor<MessageWithRecords> c = ArgumentCaptor.forClass(MessageWithRecords.class);
        verify(mockedService).send(c.capture());
        
        MessageWithRecords actual = c.getValue();
        List<MessageRecord> records = actual.getRecords();
        assertThat(records.size(), equalTo(2));
        
        MessageRecord sent = records.get(0);
        assertThat(sent.getKey(), anyOf(nullValue(), equalTo(new byte[0])));
        assertThat(MessageParser.parse(sent.getValue()).toStringFull(), equalTo(MESSAGES.get(0).toStringFull()));
        
        sent = records.get(1);
        assertThat(sent.getKey(), anyOf(nullValue(), equalTo(new byte[0])));
        assertThat(MessageParser.parse(sent.getValue()).toStringFull(), equalTo(MESSAGES.get(1).toStringFull()));
    }

    @Test
    public void thatMessageKeyIsUsed() {
      String key = UUID.randomUUID().toString();
      KeyedMessage msg = KeyedMessage.forMessageAndKey(MESSAGES.get(0), key);
      producer.send(Collections.singletonList(msg));
      
      ArgumentCaptor<MessageWithRecords> c = ArgumentCaptor.forClass(MessageWithRecords.class);
      verify(mockedService).send(c.capture());
      
      MessageWithRecords actual = c.getValue();
      List<MessageRecord> records = actual.getRecords();
      assertThat(records.size(), equalTo(1));
      
      MessageRecord sent = records.get(0);
      assertThat(new String(sent.getKey(), UTF_8), equalTo(key));
      assertThat(MessageParser.parse(sent.getValue()).toStringFull(), equalTo(MESSAGES.get(0).toStringFull()));
    }

    @Test
    public void testExceptionIfHttpExceptionStatus() {
        doThrow(new QueueProxyServiceException("couldn't request", new RuntimeException("no")))
                .when(mockedService).send(any(MessageWithRecords.class));
        thrown.expect(QueueProxyProducerException.class);

        producer.send(MESSAGES);
    }

    @Test
    public void thatEmptyListIsAcceptedButSendsNoRequestsToProxy() {
        producer.send(Collections.emptyList());
        
        verifyZeroInteractions(mockedService);
    }
}
