package io.reactivestax.active.life.canada.controller;

import io.reactivestax.active.life.canada.constant.Endpoints;
import io.reactivestax.active.life.canada.constant.Message;
import io.reactivestax.active.life.canada.dto.*;
import io.reactivestax.active.life.canada.service.AuthenticationManagementService;
import io.reactivestax.active.life.canada.service.FamilyManagementService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(Endpoints.BASE_ENDPOINT)
public class AuthenticationController {

    private final FamilyManagementService familyManagementService;
    private final AuthenticationManagementService authenticationManagementService;

    public AuthenticationController(FamilyManagementService familyManagementService,
                                    AuthenticationManagementService authenticationManagementService) {
        this.familyManagementService = familyManagementService;
        this.authenticationManagementService = authenticationManagementService;
    }

    @PostMapping(value = Endpoints.SIGNUP, produces = MediaType.APPLICATION_JSON_VALUE, consumes =
            MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<SuccessfulResponse> signUp(@RequestBody CreateMemberRequest createMemberRequest) {
        this.familyManagementService.createFamilyMember(createMemberRequest, null, true);

        return ResponseEntity.ok(SuccessfulResponse.builder().message(Message.SIGNUP_SUCCESSFUL).build());
    }

    @PostMapping(value = Endpoints.LOGIN, produces = MediaType.APPLICATION_JSON_VALUE, consumes =
            MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<LoginResponse> login(@RequestBody LoginMemberRequest loginMemberRequest) {
        LoginResponse loginResponse = authenticationManagementService.loginMember(loginMemberRequest);

        return ResponseEntity.ok(loginResponse);
    }

    @PostMapping(value = Endpoints.LOGIN_2FA, produces = MediaType.APPLICATION_JSON_VALUE, consumes =
            MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<LoginResponse> twoFactorLogin(@RequestBody TwoFactorLoginRequest twoFactorLoginRequest) {
        LoginResponse loginResponse = authenticationManagementService.twoFactorLogin(twoFactorLoginRequest);

        return ResponseEntity.ok(loginResponse);
    }

    @GetMapping(value = Endpoints.ACTIVATION, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<SuccessfulResponse> activateAccount(@PathVariable String activationId) {
        authenticationManagementService.activateMemberAccount(activationId);

        return ResponseEntity.ok(SuccessfulResponse.builder().message(Message.ACTIVATED_SUCCESSFULLY).build());
    }
}
