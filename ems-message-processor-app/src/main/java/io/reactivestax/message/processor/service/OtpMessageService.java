package io.reactivestax.message.processor.service;

import io.reactivestax.message.processor.domain.OtpMessage;
import io.reactivestax.message.processor.respository.OtpMessageRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;
import java.util.UUID;

public class OtpMessageService {
    private final OtpMessageRepository otpMessageRepository;

    @Autowired
    public OtpMessageService(OtpMessageRepository otpMessageRepository) {
        this.otpMessageRepository = otpMessageRepository;
    }

    public void processOtpMessage(String message) {
        Optional<OtpMessage> otpMessageOptional = otpMessageRepository.findById(UUID.fromString(message));
        otpMessageOptional.ifPresent(otpMessage -> System.out.println(otpMessage.getOtp()));
    }
}
