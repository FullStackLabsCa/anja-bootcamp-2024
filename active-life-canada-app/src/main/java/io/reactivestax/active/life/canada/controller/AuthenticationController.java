package io.reactivestax.active.life.canada.controller;

import io.reactivestax.active.life.canada.constant.Endpoints;
import io.reactivestax.active.life.canada.dto.RegisterMemberRequest;
import io.reactivestax.active.life.canada.service.FamilyManagementService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(Endpoints.BASE_URL)
public class AuthenticationController {

    private final FamilyManagementService familyManagementService;

    public AuthenticationController(FamilyManagementService familyManagementService) {
        this.familyManagementService = familyManagementService;
    }

    @PostMapping(Endpoints.SIGNUP)
    public void signUp(@RequestBody RegisterMemberRequest registerMemberRequest) {
        this.familyManagementService.createFamilyMember(registerMemberRequest, true);
    }
}
