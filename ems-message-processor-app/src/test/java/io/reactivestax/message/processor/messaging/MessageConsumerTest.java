package io.reactivestax.message.processor.messaging;

import io.reactivestax.message.processor.EmbeddedArtemisTestConfig;
import io.reactivestax.message.processor.service.EnsMessageService;
import io.reactivestax.message.processor.service.OtpMessageService;
import io.reactivestax.message.processor.util.DataProvider;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doNothing;

@ActiveProfiles("test")
@SpringBootTest
@Import(EmbeddedArtemisTestConfig.class)
class MessageConsumerTest {

    @Autowired
    private MessageConsumer messageConsumer;

    @MockitoBean
    private EnsMessageService ensMessageService;

    @MockitoBean
    private OtpMessageService otpMessageService;

    @MockitoBean
    private JmsTemplate jmsTemplate;

    @Value("${spring.artemis.dlq}")
    private String dlq;

    @Test
    void testConsumeFromEnsQueue() {
        doNothing().when(ensMessageService).processEnsMessage(anyString());
        messageConsumer.consumeFromEnsQueue(DataProvider.ID_STRING);
        Mockito.verify(ensMessageService, atLeastOnce()).processEnsMessage(anyString());
    }

    @Test
    void testConsumeFromOtpQueue() {
        doNothing().when(otpMessageService).processOtpMessage(anyString());
        messageConsumer.consumeFromOtpQueue(DataProvider.ID_STRING);
        Mockito.verify(otpMessageService, atLeastOnce()).processOtpMessage(anyString());
    }

    @Test
    void testSendToDLQ() {
        doNothing().when(jmsTemplate).convertAndSend(anyString(), anyString());
        messageConsumer.sendToDLQ(new RuntimeException(), DataProvider.MESSAGE);
        Mockito.verify(jmsTemplate).convertAndSend(anyString(), anyString());
    }
}
