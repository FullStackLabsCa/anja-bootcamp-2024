package io.reactivestax.ems.messaging;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class MessageProducer implements MessageProcessor {

    private final JmsTemplate jmsTemplate;

    @Autowired
    public MessageProducer(JmsTemplate jmsTemplate) {
        this.jmsTemplate = jmsTemplate;
    }

    @Override
    public void sendMessageToQueue(String queueName, UUID message) {
        jmsTemplate.send(queueName, getMessageCreator(message));
    }

    public MessageCreator getMessageCreator(UUID message) {
        return session -> session.createTextMessage(message.toString());
    }
}
