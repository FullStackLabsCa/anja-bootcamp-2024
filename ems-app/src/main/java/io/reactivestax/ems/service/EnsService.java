package io.reactivestax.ems.service;

import io.reactivestax.ems.domain.ens.EnsMessage;
import io.reactivestax.ems.dto.MessageDTO;
import io.reactivestax.ems.enums.NotificationMethod;
import io.reactivestax.ems.repository.EnsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EnsService {

    private final EnsRepository ensRepository;

    @Autowired
    public EnsService(EnsRepository ensRepository) {
        this.ensRepository = ensRepository;
    }

    public void save(MessageDTO messageDTO, NotificationMethod notificationMethod) {
        EnsMessage ensMessage = convertToEntity(messageDTO, notificationMethod);
        EnsMessage save = ensRepository.save(ensMessage);
    }

    private EnsMessage convertToEntity(MessageDTO messageDTO, NotificationMethod notificationMethod) {
        return EnsMessage.builder()
                .customerId(messageDTO.getCustomerId())
                .phone(messageDTO.getPhoneNumber())
                .message(messageDTO.getMessage())
                .notificationMethod(notificationMethod)
                .build();
    }
}
