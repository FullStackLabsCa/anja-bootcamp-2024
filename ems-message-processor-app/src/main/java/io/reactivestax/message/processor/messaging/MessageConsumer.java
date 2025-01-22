package io.reactivestax.message.processor.messaging;

import io.reactivestax.message.processor.service.EnsMessageService;
import io.reactivestax.message.processor.service.OtpMessageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

@Component
public class MessageConsumer {
    @Value("${spring.artemis.ens-queue}")
    private String ensQueue;

    @Value("${spring.artemis.otp-queue}")
    private String otpQueue;

    private final EnsMessageService ensMessageService;
    private final OtpMessageService otpMessageService;

    public MessageConsumer(EnsMessageService ensMessageService, OtpMessageService otpMessageService) {
        this.ensMessageService = ensMessageService;
        this.otpMessageService = otpMessageService;
    }

    @JmsListener(destination = "ens_queue")
    private void consumeFromEnsQueue(String message) {
        ensMessageService.processEnsMessage(message);
    }

    @JmsListener(destination = "otp_queue")
    private void consumeFromOtpQueue(String message) {
        otpMessageService.processOtpMessage(message);
    }
}
