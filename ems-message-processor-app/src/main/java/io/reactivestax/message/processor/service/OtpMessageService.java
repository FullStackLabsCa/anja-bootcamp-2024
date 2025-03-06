package io.reactivestax.message.processor.service;

import io.reactivestax.message.processor.constant.ApplicationConstant;
import io.reactivestax.message.processor.domain.OtpMessage;
import io.reactivestax.message.processor.enums.NotificationMethod;
import io.reactivestax.message.processor.respository.OtpMessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class OtpMessageService {
    private final OtpMessageRepository otpMessageRepository;
    private final TwilioService twilioService;

    @Autowired
    public OtpMessageService(OtpMessageRepository otpMessageRepository, TwilioService twilioService) {
        this.otpMessageRepository = otpMessageRepository;
        this.twilioService = twilioService;
    }

    public void processOtpMessage(String message) {
        Optional<OtpMessage> otpMessageOptional = otpMessageRepository.findById(UUID.fromString(message));
//        otpMessageOptional.ifPresent(otpMessage -> twilioService.sendToTwilio(otpMessage.getNotificationMethod(),
//                prepareOtpMessage(otpMessage.getOtp()),
//                otpMessage.getNotificationMethod() == NotificationMethod.EMAIL ? otpMessage.getEmail() : otpMessage.getPhone()));
    }

    private String prepareOtpMessage(String otp) {
        return ApplicationConstant.OTP_MESSAGE + otp;
    }
}
