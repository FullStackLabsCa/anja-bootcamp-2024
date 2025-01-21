package io.reactivestax.ems.service;

import io.reactivestax.ems.domain.OtpMessage;
import io.reactivestax.ems.dto.BaseDTO;
import io.reactivestax.ems.enums.NotificationMethod;
import io.reactivestax.ems.enums.OtpStatus;
import io.reactivestax.ems.messaging.MessageProducer;
import io.reactivestax.ems.repository.OtpRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class OtpService {

    private final OtpRepository otpRepository;
    private final MessageProducer messageProducer;
    @Value("${spring.artemis.otp-queue}")
    private String queueName;

    @Autowired
    public OtpService(OtpRepository otpRepository, MessageProducer messageProducer) {
        this.otpRepository = otpRepository;
        this.messageProducer = messageProducer;
    }

    public void save(BaseDTO baseDTO, NotificationMethod notificationMethod) {
        OtpMessage otpMessage = convertToEntity(baseDTO, notificationMethod);
        OtpMessage savedOtpMessage = this.otpRepository.save(otpMessage);
        messageProducer.sendMessageToQueue(queueName, savedOtpMessage.getId());
    }

    private OtpMessage convertToEntity(BaseDTO otpDTO, NotificationMethod notificationMethod) {
        return OtpMessage.builder()
                .customerId(otpDTO.getCustomerId())
                .phone(otpDTO.getPhoneNumber())
                .otpStatus(OtpStatus.NOT_GENERATED)
                .notificationMethod(notificationMethod)
                .generatedDateTime(System.currentTimeMillis())
                .build();
    }
}
