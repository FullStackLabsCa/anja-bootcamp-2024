package io.reactivestax.message.processor.service;

import io.reactivestax.message.processor.domain.EnsMessage;
import io.reactivestax.message.processor.respository.EnsMessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class EnsMessageService {

    private final EnsMessageRepository ensMessageRepository;

    @Autowired
    public EnsMessageService(EnsMessageRepository ensMessageRepository) {
        this.ensMessageRepository = ensMessageRepository;
    }

    public void processEnsMessage(String message) {
        Optional<EnsMessage> ensMessageOptional = ensMessageRepository.findById(UUID.fromString(message));
        ensMessageOptional.ifPresent(ensMessage -> System.out.println(ensMessage.getMessage()));
    }
}
