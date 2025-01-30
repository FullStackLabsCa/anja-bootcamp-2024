package io.reactivestax.active.life.canada.service;

import io.reactivestax.active.life.canada.constant.ExceptionMessage;
import io.reactivestax.active.life.canada.dto.CreateMemberRequest;
import io.reactivestax.active.life.canada.dto.LoginMemberRequest;
import io.reactivestax.active.life.canada.entity.FamilyGroup;
import io.reactivestax.active.life.canada.entity.FamilyMember;
import io.reactivestax.active.life.canada.enums.Status;
import io.reactivestax.active.life.canada.exception.InvalidRequestException;
import io.reactivestax.active.life.canada.exception.UnauthorizedException;
import io.reactivestax.active.life.canada.mapper.FamilyMemberMapper;
import io.reactivestax.active.life.canada.model.SecurityHeader;
import io.reactivestax.active.life.canada.repository.FamilyGroupRepository;
import io.reactivestax.active.life.canada.repository.FamilyMemberRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class FamilyManagementService {

    private final FamilyMemberRepository familyMemberRepository;
    private final FamilyGroupRepository familyGroupRepository;
    private final FamilyMemberMapper familyMemberMapper;
    private final EmsService emsService;

    public FamilyManagementService(FamilyMemberRepository familyMemberRepository,
                                   FamilyGroupRepository familyGroupRepository,
                                   FamilyMemberMapper familyMemberMapper,
                                   EmsService emsService) {
        this.familyMemberRepository = familyMemberRepository;
        this.familyGroupRepository = familyGroupRepository;
        this.familyMemberMapper = familyMemberMapper;
        this.emsService = emsService;
    }

    public void createFamilyMember(CreateMemberRequest createMemberRequest, SecurityHeader securityHeader,
                                   boolean isGroupAdmin) {
        FamilyMember familyMember = familyMemberMapper.registerMemberRequestToFamilyMember(createMemberRequest);
        Optional<FamilyMember> familyMemberOptional = familyMemberRepository.findByMemberLoginId(familyMember.getMemberLoginId());
        familyMemberOptional.ifPresentOrElse(member -> {
                    throw new InvalidRequestException(ExceptionMessage.MEMBER_ALREADY_EXISTS);
                }, () -> {
                    familyMember.setGroupAdmin(isGroupAdmin);
                    if (isGroupAdmin) {
                        FamilyGroup familyGroup = new FamilyGroup();
                        familyGroup.setFamilyPin(createMemberRequest.getPassword());
                        saveFamilyMemberAndSendToEms(familyMember, familyGroup);
                    } else {
                        Optional<FamilyMember> adminMember = familyMemberRepository.findById(UUID.fromString(securityHeader.getFamilyMemberId()));
                        adminMember.ifPresentOrElse(admin -> {
                            FamilyGroup familyGroup = admin.getFamilyGroup();
                            saveFamilyMemberAndSendToEms(familyMember, familyGroup);
                        }, () -> {
                            throw new UnauthorizedException(ExceptionMessage.UNAUTHORIZED_ACCESS);
                        });
                    }
                }
        );
    }

    private void saveFamilyMemberAndSendToEms(FamilyMember familyMember, FamilyGroup familyGroup) {
        String token = UUID.randomUUID().toString();
        familyMember.setActivationToken(token);
        familyMember.setFamilyGroup(familyGroup);
        familyGroup.getFamilyMembers().add(familyMember);
        FamilyGroup familyGroupSaved = familyGroupRepository.save(familyGroup);
        List<FamilyMember> familyMembers = familyGroupSaved.getFamilyMembers();
        FamilyMember savedFamilyMember = familyMembers.get(familyMembers.size() - 1);
        emsService.sendToEms(savedFamilyMember);
    }

    public void loginMember(LoginMemberRequest loginMemberRequest) {
        Optional<FamilyMember> familyMember = familyMemberRepository.findByMemberLoginId(loginMemberRequest.getMemberLoginId());
        familyMember.ifPresent(member -> {
            if (member.isActive()) {
//                emsService.sendToEms(member.getFamilyMemberId());
            } else {
//                emsService.sendToEms(member.getFamilyMemberId());
            }
        });
    }

    public void activateMemberAccount(String activationToken) {
        Optional<FamilyMember> familyMember = familyMemberRepository.findByActivationToken(activationToken);
        familyMember.ifPresentOrElse(member -> {
            member.setActive(true);
            if (member.isGroupAdmin()) {
                FamilyGroup familyGroup = member.getFamilyGroup();
                familyGroup.setStatus(Status.ACTIVE);
                familyGroupRepository.save(familyGroup);
            } else familyMemberRepository.save(member);
        }, () -> {
            throw new InvalidRequestException(ExceptionMessage.INVALID_ACTIVATION_LINK);
        });
    }
}
