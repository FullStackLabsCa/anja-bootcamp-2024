package io.reactivestax.active.life.canada.service;

import io.reactivestax.active.life.canada.constant.ExceptionMessage;
import io.reactivestax.active.life.canada.dto.CreateMemberRequest;
import io.reactivestax.active.life.canada.dto.MemberDetails;
import io.reactivestax.active.life.canada.dto.UpdateMemberRequest;
import io.reactivestax.active.life.canada.entity.FamilyGroup;
import io.reactivestax.active.life.canada.entity.FamilyMember;
import io.reactivestax.active.life.canada.exception.InvalidRequestException;
import io.reactivestax.active.life.canada.exception.UnauthorizedException;
import io.reactivestax.active.life.canada.mapper.FamilyMemberMapper;
import io.reactivestax.active.life.canada.repository.FamilyGroupRepository;
import io.reactivestax.active.life.canada.repository.FamilyMemberRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

@Service
public class FamilyManagementService {

    private final FamilyMemberRepository familyMemberRepository;
    private final FamilyGroupRepository familyGroupRepository;
    private final FamilyMemberMapper familyMemberMapper;
    private final ActiveLifeCommonService activeLifeCommonService;

    public FamilyManagementService(FamilyMemberRepository familyMemberRepository,
                                   FamilyGroupRepository familyGroupRepository,
                                   FamilyMemberMapper familyMemberMapper,
                                   ActiveLifeCommonService activeLifeCommonService) {
        this.familyMemberRepository = familyMemberRepository;
        this.familyGroupRepository = familyGroupRepository;
        this.familyMemberMapper = familyMemberMapper;
        this.activeLifeCommonService = activeLifeCommonService;
    }

    @Transactional
    public void createFamilyMember(CreateMemberRequest createMemberRequest, String loggedInMemberId,
                                   boolean isGroupAdmin) {
        FamilyMember familyMember = familyMemberMapper.registerMemberRequestToFamilyMember(createMemberRequest);
        familyMemberRepository.findByMemberLoginId(familyMember.getMemberLoginId())
                .ifPresent(member -> {
                    throw new InvalidRequestException(ExceptionMessage.MEMBER_ALREADY_EXISTS);
                });
        familyMember.setGroupAdmin(isGroupAdmin);
        if (isGroupAdmin) {
            FamilyGroup familyGroup = new FamilyGroup();
            familyGroup.setFamilyPin(createMemberRequest.getPassword());
            saveFamilyMemberAndSendToEms(familyMember, familyGroup);
        } else {
            Optional<FamilyMember> loggedInMember = familyMemberRepository.findById(UUID.fromString(loggedInMemberId));
            loggedInMember.ifPresentOrElse(admin -> {
                if (admin.isGroupAdmin()) {
                    FamilyGroup familyGroup = admin.getFamilyGroup();
                    saveFamilyMemberAndSendToEms(familyMember, familyGroup);
                } else throw new UnauthorizedException(ExceptionMessage.UNAUTHORIZED_ACCESS);
            }, () -> {
                throw new UnauthorizedException(ExceptionMessage.UNAUTHORIZED_ACCESS);
            });
        }
    }

    private void saveFamilyMemberAndSendToEms(FamilyMember familyMember, FamilyGroup familyGroup) {
        familyMember.setFamilyGroup(familyGroup);
        familyGroup.getFamilyMembers().add(familyMember);
        FamilyGroup familyGroupSaved = familyGroupRepository.save(familyGroup);
        List<FamilyMember> familyMembers = familyGroupSaved.getFamilyMembers();
        FamilyMember savedFamilyMember = familyMembers.get(familyMembers.size() - 1);
        activeLifeCommonService.createAccountActivationRequestEntryAndSendToEms(savedFamilyMember);
    }

    @Transactional
    public void updateFamilyMember(String memberId, UpdateMemberRequest updateMemberRequest, String loggedInMemberId) {
        familyMemberRepository.findById(UUID.fromString(loggedInMemberId)).ifPresentOrElse(member -> {
            if (memberId.equals(loggedInMemberId)) {
                Optional<FamilyMember> familyMemberOptional = familyMemberRepository.findByMemberLoginId(memberId);
                FamilyMember familyMember = familyMemberOptional
                        .orElseThrow(() -> new InvalidRequestException(ExceptionMessage.INVALID_MEMBER_ID));
                familyMemberMapper.updateMemberRequestToFamilyMember(updateMemberRequest, familyMember);
                familyMemberRepository.save(familyMember);
            } else throw new UnauthorizedException(ExceptionMessage.UNAUTHORIZED_ACCESS);
        }, () -> {
            throw new UnauthorizedException(ExceptionMessage.UNAUTHORIZED_ACCESS);
        });
    }

    public MemberDetails getFamilyMember(String memberId, String loggedInMemberId) {
        AtomicReference<MemberDetails> memberDetails = new AtomicReference<>(new MemberDetails());
        familyMemberRepository.findById(UUID.fromString(loggedInMemberId)).ifPresentOrElse(member -> {
            if (memberId.equals(loggedInMemberId)) {
                Optional<FamilyMember> familyMemberOptional = familyMemberRepository.findByMemberLoginId(memberId);
                FamilyMember familyMember = familyMemberOptional
                        .orElseThrow(() -> new InvalidRequestException(ExceptionMessage.INVALID_MEMBER_ID));
                Double credits = familyMember.getFamilyGroup().getCredits();
                memberDetails.set(familyMemberMapper.familyMemberToMemberDetails(familyMember));
                memberDetails.get().setCredits(credits);
            } else throw new UnauthorizedException(ExceptionMessage.UNAUTHORIZED_ACCESS);
        }, () -> {
            throw new UnauthorizedException(ExceptionMessage.UNAUTHORIZED_ACCESS);
        });

        return memberDetails.get();
    }

    @Transactional
    public void deactivateFamilyMember(String memberLoginId, String loggedInMemberId) {
        FamilyMember loggedInMember = familyMemberRepository.findById(UUID.fromString(loggedInMemberId))
                .orElseThrow(() -> new UnauthorizedException(ExceptionMessage.UNAUTHORIZED_ACCESS));
        if (memberLoginId.equals(loggedInMemberId)) {
            familyMemberRepository.updateIsActiveByFamilyMemberId(loggedInMember.getFamilyMemberId(), false);
        } else if (loggedInMember.isGroupAdmin()) {
            if (familyMemberRepository.existsByMemberLoginIdAndFamilyGroup_FamilyGroupId(memberLoginId, loggedInMember.getFamilyGroup().getFamilyGroupId())) {
                familyMemberRepository.updateIsActiveByMemberLoginId(memberLoginId, false);
            } else throw new UnauthorizedException(ExceptionMessage.INVALID_MEMBER_ID);
        } else throw new UnauthorizedException(ExceptionMessage.UNAUTHORIZED_ACCESS);
    }
}
