package io.reactivestax.ems.messaging;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@SpringBootTest
 class MessageProducerTest {

    @Autowired
    private MessageProducer messageProducer;

    @MockitoBean
    private JmsTemplate jmsTemplate;

    @Test
    void testSendMessageToQueue(){
        doNothing().when(jmsTemplate).send(anyString(), any(MessageCreator.class));
        messageProducer.sendMessageToQueue("message", UUID.fromString("b87ce6bd-ec3c-4d51-9c48-b68ef99301ee"));
        verify(jmsTemplate, atLeastOnce()).send(anyString(), any(MessageCreator.class));
    }

    @Test
    void testGetMessageCreator(){
        MessageCreator messageCreator = messageProducer.getMessageCreator(UUID.fromString("b87ce6bd-ec3c-4d51-9c48-b68ef99301ee"));
        assertThat(messageCreator).isInstanceOf(MessageCreator.class);
    }
}
