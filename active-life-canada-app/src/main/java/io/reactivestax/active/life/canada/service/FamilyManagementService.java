package io.reactivestax.active.life.canada.service;

import io.reactivestax.active.life.canada.constant.ExceptionHandlerConst;
import io.reactivestax.active.life.canada.dto.CreateMemberRequest;
import io.reactivestax.active.life.canada.dto.MemberDetails;
import io.reactivestax.active.life.canada.dto.UpdateMemberRequest;
import io.reactivestax.active.life.canada.entity.FamilyGroup;
import io.reactivestax.active.life.canada.entity.FamilyMember;
import io.reactivestax.active.life.canada.exception.InvalidRequestException;
import io.reactivestax.active.life.canada.exception.UnauthorizedAccessException;
import io.reactivestax.active.life.canada.mapper.FamilyMemberMapper;
import io.reactivestax.active.life.canada.repository.FamilyGroupRepository;
import io.reactivestax.active.life.canada.repository.FamilyMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FamilyManagementService {

    private final FamilyMemberRepository familyMemberRepository;
    private final FamilyGroupRepository familyGroupRepository;
    private final FamilyMemberMapper familyMemberMapper;
    private final AsyncJobsService asyncJobsService;

    @Transactional
    public void createFamilyMember(CreateMemberRequest createMemberRequest, String loggedInMemberId, boolean isGroupAdmin) {
        FamilyMember familyMember = familyMemberMapper.registerMemberRequestToFamilyMember(createMemberRequest);
        if (familyMemberRepository.existsByMemberLoginId(familyMember.getMemberLoginId()))
            throw new InvalidRequestException(ExceptionHandlerConst.MEMBER_ALREADY_EXISTS);
        familyMember.setGroupAdmin(isGroupAdmin);
        if (isGroupAdmin) {
            FamilyGroup familyGroup = new FamilyGroup();
            familyGroup.setFamilyPin(createMemberRequest.getPassword());
            saveFamilyMemberAndSendToEms(familyMember, familyGroup);
        } else {
            FamilyMember loggedInMember =
                    familyMemberRepository.findByFamilyMemberIdAndIsActive(UUID.fromString(loggedInMemberId), true)
                            .orElseThrow(() -> new UnauthorizedAccessException(ExceptionHandlerConst.UNAUTHORIZED_ACCESS));
            if (loggedInMember.isGroupAdmin()) {
                FamilyGroup familyGroup = loggedInMember.getFamilyGroup();
                saveFamilyMemberAndSendToEms(familyMember, familyGroup);
            } else throw new UnauthorizedAccessException(ExceptionHandlerConst.UNAUTHORIZED_ACCESS);
        }
    }

    private void saveFamilyMemberAndSendToEms(FamilyMember familyMember, FamilyGroup familyGroup) {
        familyMember.setFamilyGroup(familyGroup);
        familyGroup.getFamilyMembers().add(familyMember);
        FamilyGroup familyGroupSaved = familyGroupRepository.save(familyGroup);
        List<FamilyMember> familyMembers = familyGroupSaved.getFamilyMembers();
        FamilyMember savedFamilyMember = familyMembers.get(familyMembers.size() - 1);
        asyncJobsService.createAccountActivationRequestEntryAndSendToEms(savedFamilyMember);
    }

    @Transactional
    public void updateFamilyMember(String memberLoginId, UpdateMemberRequest updateMemberRequest, String loggedInMemberId) {
        FamilyMember loggedInMember = familyMemberRepository.findByFamilyMemberIdAndIsActive(UUID.fromString(loggedInMemberId), true)
                .orElseThrow(() -> new UnauthorizedAccessException(ExceptionHandlerConst.UNAUTHORIZED_ACCESS));
        if (memberLoginId.equals(loggedInMemberId)) {
            convertToEntityAndUpdateFamilyMember(loggedInMember, updateMemberRequest);
        } else if (loggedInMember.isGroupAdmin()) {
            FamilyMember familyMember = familyMemberRepository.findByMemberLoginIdAndIsActiveAndFamilyGroup_FamilyGroupId
                            (memberLoginId, true, loggedInMember.getFamilyGroup().getFamilyGroupId())
                    .orElseThrow(() -> new InvalidRequestException(ExceptionHandlerConst.INVALID_MEMBER_ID));
            convertToEntityAndUpdateFamilyMember(familyMember, updateMemberRequest);
        } else throw new UnauthorizedAccessException(ExceptionHandlerConst.UNAUTHORIZED_ACCESS);
    }

    private void convertToEntityAndUpdateFamilyMember(FamilyMember familyMember, UpdateMemberRequest updateMemberRequest) {
        familyMemberMapper.updateMemberRequestToFamilyMember(updateMemberRequest, familyMember);
        familyMemberRepository.save(familyMember);
    }

    public MemberDetails getFamilyMember(String memberLoginId, String loggedInMemberId) {
        FamilyMember loggedInMember = familyMemberRepository.findByFamilyMemberIdAndIsActive(UUID.fromString(loggedInMemberId), true)
                .orElseThrow(() -> new UnauthorizedAccessException(ExceptionHandlerConst.UNAUTHORIZED_ACCESS));
        if (memberLoginId.equals(loggedInMemberId)) {
            return getMemberDetails(loggedInMember);
        } else if (loggedInMember.isGroupAdmin()) {
            FamilyMember familyMember = familyMemberRepository.findByMemberLoginIdAndIsActiveAndFamilyGroup_FamilyGroupId
                            (memberLoginId, true, loggedInMember.getFamilyGroup().getFamilyGroupId())
                    .orElseThrow(() -> new InvalidRequestException(ExceptionHandlerConst.INVALID_MEMBER_ID));
            return getMemberDetails(familyMember);
        } else throw new UnauthorizedAccessException(ExceptionHandlerConst.UNAUTHORIZED_ACCESS);
    }

    private MemberDetails getMemberDetails(FamilyMember familyMember) {
        Double credits = familyMember.getFamilyGroup().getCredits();
        MemberDetails memberDetails = familyMemberMapper.familyMemberToMemberDetails(familyMember);
        memberDetails.setCredits(credits);
        return memberDetails;
    }

    @Transactional
    public void deactivateFamilyMember(String memberLoginId, String loggedInMemberId) {
        FamilyMember loggedInMember = familyMemberRepository.findByFamilyMemberIdAndIsActive(UUID.fromString(loggedInMemberId), true)
                .orElseThrow(() -> new UnauthorizedAccessException(ExceptionHandlerConst.UNAUTHORIZED_ACCESS));
        if (memberLoginId.equals(loggedInMemberId)) {
            familyMemberRepository.updateIsActiveByFamilyMemberId(loggedInMember.getFamilyMemberId(), false);
        } else if (loggedInMember.isGroupAdmin()) {
            if (familyMemberRepository.existsByMemberLoginIdAndFamilyGroup_FamilyGroupId(memberLoginId, loggedInMember.getFamilyGroup().getFamilyGroupId())) {
                familyMemberRepository.updateIsActiveByMemberLoginId(memberLoginId, false);
            } else throw new UnauthorizedAccessException(ExceptionHandlerConst.INVALID_MEMBER_ID);
        } else throw new UnauthorizedAccessException(ExceptionHandlerConst.UNAUTHORIZED_ACCESS);
    }
}
