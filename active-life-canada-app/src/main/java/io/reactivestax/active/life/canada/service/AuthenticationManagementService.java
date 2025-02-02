package io.reactivestax.active.life.canada.service;

import io.reactivestax.active.life.canada.constant.ExceptionHandlerConst;
import io.reactivestax.active.life.canada.constant.Message;
import io.reactivestax.active.life.canada.dto.LoginMemberRequest;
import io.reactivestax.active.life.canada.dto.LoginResponse;
import io.reactivestax.active.life.canada.dto.TwoFactorLoginRequest;
import io.reactivestax.active.life.canada.entity.AccountActivationRequest;
import io.reactivestax.active.life.canada.entity.FamilyMember;
import io.reactivestax.active.life.canada.entity.LoginRequest;
import io.reactivestax.active.life.canada.exception.InvalidRequestException;
import io.reactivestax.active.life.canada.exception.SomethingWentWrongException;
import io.reactivestax.active.life.canada.repository.AccountActivationRequestRepository;
import io.reactivestax.active.life.canada.repository.FamilyMemberRepository;
import io.reactivestax.active.life.canada.repository.LoginRequestRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class AuthenticationManagementService {

    private final FamilyMemberRepository familyMemberRepository;
    private final LoginRequestRepository loginRequestRepository;
    private final AccountActivationRequestRepository accountActivationRequestRepository;
    private final EmsService emsService;
    private final ActiveLifeCommonService activeLifeCommonService;

    public AuthenticationManagementService(FamilyMemberRepository familyMemberRepository,
                                           LoginRequestRepository loginRequestRepository,
                                           AccountActivationRequestRepository accountActivationRequestRepository,
                                           EmsService emsService,
                                           ActiveLifeCommonService activeLifeCommonService) {
        this.familyMemberRepository = familyMemberRepository;
        this.loginRequestRepository = loginRequestRepository;
        this.accountActivationRequestRepository = accountActivationRequestRepository;
        this.emsService = emsService;
        this.activeLifeCommonService = activeLifeCommonService;
    }

    @Transactional
    public LoginResponse loginMember(LoginMemberRequest loginMemberRequest) {
        String token;
        String message;
        FamilyMember familyMember = familyMemberRepository.findByMemberLoginId(loginMemberRequest.getUsername())
                .orElseThrow(() -> new InvalidRequestException(ExceptionHandlerConst.INCORRECT_USERNAME_PASSWORD));
        String familyPin = familyMember.getFamilyGroup().getFamilyPin();
        if (loginMemberRequest.getPassword().equals(familyPin)) {
            token = UUID.randomUUID().toString();
            if (familyMember.isActive()) {
                LoginRequest loginRequest = LoginRequest.builder()
                        .familyMemberId(familyMember.getFamilyMemberId())
                        .loginToken(token)
                        .build();
                loginRequestRepository.save(loginRequest);
                new Thread(() -> emsService.sendToEmsOtp(familyMember));
                message = Message.SUCCESSFUL_LOGIN;
            } else {
                message = Message.LOGIN_INACTIVE_MEMBER;
                new Thread(() -> activeLifeCommonService.createAccountActivationRequestEntryAndSendToEms(familyMember));
            }
        } else throw new InvalidRequestException(ExceptionHandlerConst.INCORRECT_USERNAME_PASSWORD);
        return LoginResponse.builder().token(token).message(message).build();
    }

    public LoginResponse twoFactorLogin(TwoFactorLoginRequest twoFactorLoginRequest) {
        LoginRequest loginRequest = loginRequestRepository.findByLoginToken(twoFactorLoginRequest.getToken())
                .orElseThrow(() -> new InvalidRequestException(ExceptionHandlerConst.INCORRECT_TOKEN_OTP));
        if (emsService.sendToEmsForVerification(loginRequest.getFamilyMemberId().toString(), twoFactorLoginRequest.getOtp())) {
            return LoginResponse.builder().token(loginRequest.getFamilyMemberId().toString())
                    .message(Message.SUCCESSFUL_LOGIN_VERIFICATION).build();
        }
        throw new SomethingWentWrongException(ExceptionHandlerConst.VERIFICATION_FAILED);
    }

    @Transactional
    public void activateMemberAccount(String activationToken) {
        AccountActivationRequest accountActivationRequest = accountActivationRequestRepository.
                findByToken(UUID.fromString(activationToken))
                .orElseThrow(() -> new InvalidRequestException(ExceptionHandlerConst.INVALID_ACTIVATION_LINK));
        familyMemberRepository.updateIsActiveByFamilyMemberId(accountActivationRequest.getFamilyMemberId(), true);
    }
}
