package io.reactivestax.message.processor.service;

import io.reactivestax.message.processor.domain.EnsMessage;
import io.reactivestax.message.processor.enums.NotificationMethod;
import io.reactivestax.message.processor.respository.EnsMessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class EnsMessageService {

    private final EnsMessageRepository ensMessageRepository;
    private final TwilioService twilioService;

    @Autowired
    public EnsMessageService(EnsMessageRepository ensMessageRepository, TwilioService twilioService) {
        this.ensMessageRepository = ensMessageRepository;
        this.twilioService = twilioService;
    }

    public void processEnsMessage(String message) {
//        Optional<EnsMessage> ensMessageOptional = ensMessageRepository.findById(UUID.fromString(message));
//        ensMessageOptional.ifPresent(ensMessage ->
//                twilioService.sendToTwilio(ensMessage.getNotificationMethod(),
//                        ensMessage.getMessage(),
//                        ensMessage.getNotificationMethod() == NotificationMethod.EMAIL
//                                ? ensMessage.getEmail() : ensMessage.getPhone()));
    }
}
