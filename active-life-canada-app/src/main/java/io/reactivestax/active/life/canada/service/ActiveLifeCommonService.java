package io.reactivestax.active.life.canada.service;

import io.reactivestax.active.life.canada.constant.Endpoints;
import io.reactivestax.active.life.canada.constant.Message;
import io.reactivestax.active.life.canada.entity.AccountActivationRequest;
import io.reactivestax.active.life.canada.entity.FamilyMember;
import io.reactivestax.active.life.canada.entity.OfferedCourseWaitlist;
import io.reactivestax.active.life.canada.repository.AccountActivationRequestRepository;
import io.reactivestax.active.life.canada.repository.OfferedCourseWaitlistRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.MessageFormat;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ActiveLifeCommonService {

    private final AccountActivationRequestRepository accountActivationRequestRepository;
    private final OfferedCourseWaitlistRepository offeredCourseWaitlistRepository;
    private final EmsService emsService;

    @Async
    @Transactional
    public void createAccountActivationRequestEntryAndSendToEms(FamilyMember familyMember) {
        AccountActivationRequest accountActivationRequest = AccountActivationRequest.builder()
                .familyMemberId(familyMember.getFamilyMemberId())
                .token(UUID.randomUUID())
                .build();
        AccountActivationRequest savedAccountActivationRequest = accountActivationRequestRepository.save(accountActivationRequest);
        String activationLink = MessageFormat.format(Endpoints.ACTIVATION_LINK_URL, savedAccountActivationRequest.getToken());
        String message = MessageFormat.format(Message.ACTIVATION_LINK_MESSAGE, familyMember.getName(), activationLink);
        emsService.sendToEms(familyMember, message);
    }

    @Async
    public void getAllWaitlistedMembersByOfferedCourseIdAndSendToEms(UUID offeredCourseId, String courseName) {
        List<FamilyMember> allWaitlistedMembersByOfferedCourseId = offeredCourseWaitlistRepository
                .findAllByOfferedCourse_OfferedCourseId(offeredCourseId)
                .stream().map(OfferedCourseWaitlist::getFamilyMember).toList();
        allWaitlistedMembersByOfferedCourseId.forEach(familyMember -> emsService.sendToEms(familyMember,
                MessageFormat.format(Message.SPOT_AVAILABLE_FOR_ENROLLMENT, familyMember.getName(), courseName)));
    }

    @Async
    public void removeEntryFromWaitlistIfExists(UUID offeredCourseId, UUID familyMemberId) {
        offeredCourseWaitlistRepository.deleteFromWaitlistByFamilyMemberIdAndOfferedCourseId(offeredCourseId, familyMemberId);
    }
}
