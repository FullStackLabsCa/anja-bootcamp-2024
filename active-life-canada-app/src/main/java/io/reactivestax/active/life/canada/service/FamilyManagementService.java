package io.reactivestax.active.life.canada.service;

import io.reactivestax.active.life.canada.constant.ExceptionMessage;
import io.reactivestax.active.life.canada.constant.Message;
import io.reactivestax.active.life.canada.dto.CreateMemberRequest;
import io.reactivestax.active.life.canada.dto.LoginMemberRequest;
import io.reactivestax.active.life.canada.dto.LoginResponse;
import io.reactivestax.active.life.canada.dto.TwoFactorLoginRequest;
import io.reactivestax.active.life.canada.entity.FamilyGroup;
import io.reactivestax.active.life.canada.entity.FamilyMember;
import io.reactivestax.active.life.canada.entity.LoginRequest;
import io.reactivestax.active.life.canada.enums.Status;
import io.reactivestax.active.life.canada.exception.InvalidRequestException;
import io.reactivestax.active.life.canada.exception.UnauthorizedException;
import io.reactivestax.active.life.canada.mapper.FamilyMemberMapper;
import io.reactivestax.active.life.canada.model.SecurityHeader;
import io.reactivestax.active.life.canada.repository.FamilyGroupRepository;
import io.reactivestax.active.life.canada.repository.FamilyMemberRepository;
import io.reactivestax.active.life.canada.repository.LoginRequestRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class FamilyManagementService {

    private final FamilyMemberRepository familyMemberRepository;
    private final FamilyGroupRepository familyGroupRepository;
    private final FamilyMemberMapper familyMemberMapper;
    private final LoginRequestRepository loginRequestRepository;
    private final EmsService emsService;

    public FamilyManagementService(FamilyMemberRepository familyMemberRepository,
                                   FamilyGroupRepository familyGroupRepository,
                                   FamilyMemberMapper familyMemberMapper,
                                   LoginRequestRepository loginRequestRepository,
                                   EmsService emsService) {
        this.familyMemberRepository = familyMemberRepository;
        this.familyGroupRepository = familyGroupRepository;
        this.familyMemberMapper = familyMemberMapper;
        this.loginRequestRepository = loginRequestRepository;
        this.emsService = emsService;
    }

    public void createFamilyMember(CreateMemberRequest createMemberRequest, SecurityHeader securityHeader,
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
            Optional<FamilyMember> adminMember = familyMemberRepository.findById(UUID.fromString(securityHeader.getFamilyMemberId()));
            adminMember.ifPresentOrElse(admin -> {
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
        String token = UUID.randomUUID().toString();
        familyMember.setActivationToken(token);
        familyMember.setFamilyGroup(familyGroup);
        familyGroup.getFamilyMembers().add(familyMember);
        FamilyGroup familyGroupSaved = familyGroupRepository.save(familyGroup);
        List<FamilyMember> familyMembers = familyGroupSaved.getFamilyMembers();
        FamilyMember savedFamilyMember = familyMembers.get(familyMembers.size() - 1);
        emsService.sendToEms(savedFamilyMember);
    }

    public LoginResponse loginMember(LoginMemberRequest loginMemberRequest) {
        String token = "";
        String message = "";
        FamilyMember familyMember =
                familyMemberRepository.findByMemberLoginId(loginMemberRequest.getUsername())
                        .orElseThrow(() -> new InvalidRequestException(ExceptionMessage.INCORRECT_USERNAME_PASSWORD));
        String familyPin = familyMember.getFamilyGroup().getFamilyPin();
        if (loginMemberRequest.getPassword().equals(familyPin)) {
            token = UUID.randomUUID().toString();
            if (familyMember.isActive()) {
                LoginRequest loginRequest = LoginRequest.builder()
                        .familyMemberId(familyMember.getFamilyMemberId())
                        .loginToken(token)
                        .build();
                loginRequestRepository.save(loginRequest);
                emsService.sendToEmsOtp(familyMember);
                message = Message.SUCCESSFUL_LOGIN;
            } else {
                familyMember.setActivationToken(token);
                FamilyMember savedMember = familyMemberRepository.save(familyMember);
                emsService.sendToEms(savedMember);
                message = Message.LOGIN_INACTIVE_MEMBER;
            }
        } else throw new InvalidRequestException(ExceptionMessage.INCORRECT_USERNAME_PASSWORD);
        return LoginResponse.builder().token(token).message(message).build();
    }

    public LoginResponse twoFactorLogin(TwoFactorLoginRequest twoFactorLoginRequest) {
        LoginRequest loginRequest = loginRequestRepository.findByLoginToken(twoFactorLoginRequest.getToken())
                .orElseThrow(() -> new InvalidRequestException(ExceptionMessage.INCORRECT_TOKEN_OTP));
        emsService.sendToEmsForVerification(loginRequest.getFamilyMemberId().toString(),
                twoFactorLoginRequest.getOtp());

        return LoginResponse.builder().message(Message.SUCCESSFUL_LOGIN_VERIFICATION).build();
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
