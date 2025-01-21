package io.reactivestax.ems.service;

import io.reactivestax.ems.domain.Customer;
import io.reactivestax.ems.domain.EnsMessage;
import io.reactivestax.ems.dto.BaseDTO;
import io.reactivestax.ems.enums.MessageStatus;
import io.reactivestax.ems.enums.NotificationMethod;
import io.reactivestax.ems.exception.InvalidRequestException;
import io.reactivestax.ems.messaging.MessageProducer;
import io.reactivestax.ems.repository.EnsRepository;
import io.reactivestax.ems.util.EmsUtil;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class EnsService implements MessagingService {

    private final EnsRepository ensRepository;
    private final MessageProducer messageProducer;
    private final EmsUtil emsUtil;
    @Value("${spring.artemis.ens-queue}")
    private String queueName;

    @Autowired
    public EnsService(EnsRepository ensRepository, MessageProducer messageProducer, EmsUtil emsUtil) {
        this.ensRepository = ensRepository;
        this.messageProducer = messageProducer;
        this.emsUtil = emsUtil;
    }

    @Transactional
    @Override
    public void save(BaseDTO messageDTO, NotificationMethod notificationMethod) {
        Customer customer = emsUtil.checkIfCustomerExists(messageDTO.getCustomerId());
        String contact = emsUtil.getContact(messageDTO.getPhoneNumber(), messageDTO.getEmail(), notificationMethod);
        if (emsUtil.checkIfProvidedContactExistInContacts(contact, customer.getContacts())) {
            EnsMessage ensMessage = convertToEntity(messageDTO, notificationMethod);
            EnsMessage savedMessage = ensRepository.save(ensMessage);
            messageProducer.sendMessageToQueue(queueName, savedMessage.getId());
        } else throw new InvalidRequestException(emsUtil.getValidationMessageForInvalidContact(notificationMethod));
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
