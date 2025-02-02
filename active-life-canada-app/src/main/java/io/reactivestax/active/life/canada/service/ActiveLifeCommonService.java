package io.reactivestax.active.life.canada.service;

import io.reactivestax.active.life.canada.constant.Endpoints;
import io.reactivestax.active.life.canada.constant.Message;
import io.reactivestax.active.life.canada.entity.AccountActivationRequest;
import io.reactivestax.active.life.canada.entity.FamilyMember;
import io.reactivestax.active.life.canada.repository.AccountActivationRequestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.MessageFormat;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ActiveLifeCommonService {

    private final AccountActivationRequestRepository accountActivationRequestRepository;
    private final EmsService emsService;

    @Transactional
    public void createAccountActivationRequestEntryAndSendToEms(FamilyMember familyMember) {
        AccountActivationRequest accountActivationRequest = AccountActivationRequest.builder()
                .familyMemberId(familyMember.getFamilyMemberId())
                .token(UUID.randomUUID())
                .build();
        AccountActivationRequest savedAccountActivationRequest = accountActivationRequestRepository.save(accountActivationRequest);
        String activationLink = MessageFormat.format(Endpoints.ACTIVATION_LINK_URL, savedAccountActivationRequest.getToken());
        String message = MessageFormat.format(Message.ACTIVATION_LINK_MESSAGE, familyMember.getName(), activationLink);
        new Thread(() -> emsService.sendToEms(familyMember, message));
    }
}
