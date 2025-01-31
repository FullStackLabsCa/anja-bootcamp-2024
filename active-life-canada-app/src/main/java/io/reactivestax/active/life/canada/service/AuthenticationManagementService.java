package io.reactivestax.active.life.canada.service;

import io.reactivestax.active.life.canada.constant.ExceptionMessage;
import io.reactivestax.active.life.canada.constant.Message;
import io.reactivestax.active.life.canada.dto.LoginMemberRequest;
import io.reactivestax.active.life.canada.dto.LoginResponse;
import io.reactivestax.active.life.canada.dto.TwoFactorLoginRequest;
import io.reactivestax.active.life.canada.entity.FamilyGroup;
import io.reactivestax.active.life.canada.entity.FamilyMember;
import io.reactivestax.active.life.canada.entity.LoginRequest;
import io.reactivestax.active.life.canada.enums.Status;
import io.reactivestax.active.life.canada.exception.InvalidRequestException;
import io.reactivestax.active.life.canada.repository.FamilyGroupRepository;
import io.reactivestax.active.life.canada.repository.FamilyMemberRepository;
import io.reactivestax.active.life.canada.repository.LoginRequestRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
public class AuthenticationManagementService {

    private final FamilyMemberRepository familyMemberRepository;
    private final LoginRequestRepository loginRequestRepository;
    private final FamilyGroupRepository familyGroupRepository;
    private final EmsService emsService;

    public AuthenticationManagementService(FamilyMemberRepository familyMemberRepository,
                                           LoginRequestRepository loginRequestRepository,
                                           FamilyGroupRepository familyGroupRepository,
                                           EmsService emsService) {
        this.familyMemberRepository = familyMemberRepository;
        this.loginRequestRepository = loginRequestRepository;
        this.familyGroupRepository = familyGroupRepository;
        this.emsService = emsService;
    }

    @Transactional
    public LoginResponse loginMember(LoginMemberRequest loginMemberRequest) {
        String token;
        String message;
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

    @Transactional
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
