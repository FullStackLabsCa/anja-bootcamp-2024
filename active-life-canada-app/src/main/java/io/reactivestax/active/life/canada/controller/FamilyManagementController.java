package io.reactivestax.active.life.canada.controller;

import io.reactivestax.active.life.canada.constant.Endpoints;
import io.reactivestax.active.life.canada.constant.Message;
import io.reactivestax.active.life.canada.dto.CreateMemberRequest;
import io.reactivestax.active.life.canada.dto.MemberDetails;
import io.reactivestax.active.life.canada.dto.SuccessfulResponse;
import io.reactivestax.active.life.canada.dto.UpdateMemberRequest;
import io.reactivestax.active.life.canada.dto.group.CreateGroup;
import io.reactivestax.active.life.canada.service.AuthenticationManagementService;
import io.reactivestax.active.life.canada.service.FamilyManagementService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(Endpoints.BASE_ENDPOINT + Endpoints.MEMBERS_BASE)
@RequiredArgsConstructor
public class FamilyManagementController {

    private final FamilyManagementService familyManagementService;
    private final AuthenticationManagementService authenticationManagementService;

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<SuccessfulResponse> addMember(@Validated(CreateGroup.class) @RequestBody CreateMemberRequest createMemberRequest) {
        this.familyManagementService.createFamilyMember(createMemberRequest,
                authenticationManagementService.getLoggedInMemberUsername(), false);

        return ResponseEntity.ok(SuccessfulResponse.builder().message(Message.MEMBER_ADD_SUCCESSFUL).build());
    }

    @PatchMapping(value = Endpoints.MEMBER_ID, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<SuccessfulResponse> updateMember(@PathVariable String memberId,
                                                           @Valid @RequestBody UpdateMemberRequest updateMemberRequest) {
        this.familyManagementService.updateFamilyMember(memberId, updateMemberRequest,
                authenticationManagementService.getLoggedInMemberUsername());

        return ResponseEntity.ok(SuccessfulResponse.builder().message(Message.MEMBER_UPDATED).build());
    }

    @GetMapping(value = Endpoints.MEMBER_ID, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<MemberDetails> getMember(@PathVariable String memberId) {
        MemberDetails memberDetails = this.familyManagementService
                .getFamilyMember(memberId, authenticationManagementService.getLoggedInMemberUsername());

        return ResponseEntity.ok(memberDetails);
    }

    @DeleteMapping(value = Endpoints.MEMBER_ID, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<SuccessfulResponse> deactivateMember(@PathVariable String memberId) {
        this.familyManagementService.deactivateFamilyMember(memberId,
                authenticationManagementService.getLoggedInMemberUsername());

        return ResponseEntity.ok(SuccessfulResponse.builder().message(Message.MEMBER_DEACTIVATED).build());
    }
}
