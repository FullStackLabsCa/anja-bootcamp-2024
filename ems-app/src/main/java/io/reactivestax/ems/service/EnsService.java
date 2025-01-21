package io.reactivestax.ems.service;

import io.reactivestax.ems.domain.EnsMessage;
import io.reactivestax.ems.dto.BaseDTO;
import io.reactivestax.ems.enums.MessageStatus;
import io.reactivestax.ems.enums.NotificationMethod;
import io.reactivestax.ems.messaging.MessageProducer;
import io.reactivestax.ems.repository.EnsRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class EnsService  implements MessagingService{

    private final EnsRepository ensRepository;
    private final MessageProducer messageProducer;
    @Value("${spring.artemis.ens-queue}")
    private String queueName;

    @Autowired
    public EnsService(EnsRepository ensRepository, MessageProducer messageProducer) {
        this.ensRepository = ensRepository;
        this.messageProducer = messageProducer;
    }

    @Transactional
    @Override
    public void save(BaseDTO messageDTO, NotificationMethod notificationMethod) {
        EnsMessage ensMessage = convertToEntity(messageDTO, notificationMethod);
        EnsMessage savedMessage = ensRepository.save(ensMessage);
        messageProducer.sendMessageToQueue(queueName, savedMessage.getId());
    }

    private EnsMessage convertToEntity(BaseDTO messageDTO, NotificationMethod notificationMethod) {
        return EnsMessage.builder()
                .customerId(messageDTO.getCustomerId())
                .phone(messageDTO.getPhoneNumber())
                .message(messageDTO.getMessage())
                .messageStatus(MessageStatus.NOT_SENT)
                .notificationMethod(notificationMethod)
                .build();
    }
}
