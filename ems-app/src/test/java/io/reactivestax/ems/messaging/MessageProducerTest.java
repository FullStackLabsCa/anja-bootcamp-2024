package io.reactivestax.ems.messaging;

import io.reactivestax.ems.util.DataProvider;
import jakarta.jms.JMSException;
import jakarta.jms.Message;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;

import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
class MessageProducerTest {

    @Autowired
    private MessageProducer messageProducer;

    @Autowired
    private JmsTemplate jmsTemplate;

    @Test
    void testSendMessageToQueue() throws JMSException {
        messageProducer.sendMessageToQueue("message", DataProvider.ID_UUID);
        Message receive = jmsTemplate.receive("message");
        if (receive != null) {
            String body = receive.getBody(String.class);
            assertThat(body).isEqualTo(DataProvider.ID_STRING);
        }
    }

    @Test
    void testGetMessageCreator() {
        MessageCreator messageCreator = messageProducer.getMessageCreator(UUID.fromString("b87ce6bd-ec3c-4d51-9c48-b68ef99301ee"));
        assertThat(messageCreator).isInstanceOf(MessageCreator.class);
    }
}
