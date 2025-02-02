package io.reactivestax.active.life.canada.service;

import io.reactivestax.active.life.canada.entity.FamilyMember;
import io.reactivestax.active.life.canada.entity.AccountActivationRequest;
import io.reactivestax.active.life.canada.repository.AccountActivationRequestRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class ActiveLifeCommonService {

    private final AccountActivationRequestRepository accountActivationRequestRepository;
    private final EmsService emsService;

    public ActiveLifeCommonService(AccountActivationRequestRepository accountActivationRequestRepository,
                                   EmsService emsService) {
        this.accountActivationRequestRepository = accountActivationRequestRepository;
        this.emsService = emsService;
    }

    public void createAccountActivationRequestEntryAndSendToEms(FamilyMember familyMember){
        AccountActivationRequest accountActivationRequest = AccountActivationRequest.builder()
                .familyMemberId(familyMember.getFamilyMemberId())
                .token(UUID.randomUUID())
                .build();
        AccountActivationRequest savedAccountActivationRequest = accountActivationRequestRepository.save(accountActivationRequest);
        emsService.sendToEms(savedAccountActivationRequest, familyMember);
    }
}
