package io.reactivestax.message.processor.messaging;

import io.reactivestax.message.processor.service.EnsMessageService;
import io.reactivestax.message.processor.service.OtpMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;

@Component
public class MessageConsumer {

    private final JmsTemplate jmsTemplate;
    private final EnsMessageService ensMessageService;
    private final OtpMessageService otpMessageService;
    @Value("${spring.artemis.dlq}")
    private String dlq;

    @Autowired
    public MessageConsumer(EnsMessageService ensMessageService,
                           OtpMessageService otpMessageService,
                           JmsTemplate jmsTemplate) {
        this.ensMessageService = ensMessageService;
        this.otpMessageService = otpMessageService;
        this.jmsTemplate = jmsTemplate;
    }

    @Retryable(
            retryFor = {Exception.class},
            backoff = @Backoff(delay = 2000, multiplier = 2)
    )
    @JmsListener(destination = "${spring.artemis.ens-queue}")
    public void consumeFromEnsQueue(String message) {
        ensMessageService.processEnsMessage(message);
    }

    @Retryable(
            retryFor = {Exception.class},
            backoff = @Backoff(delay = 2000, multiplier = 2)
    )
    @JmsListener(destination = "${spring.artemis.otp-queue}")
    public void consumeFromOtpQueue(String message) {
        otpMessageService.processOtpMessage(message);
    }

    @Recover
    void sendToDLQ(RuntimeException e, String message) {
        jmsTemplate.convertAndSend(dlq, message);
    }
}
