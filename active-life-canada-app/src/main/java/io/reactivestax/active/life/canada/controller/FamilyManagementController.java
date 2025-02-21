package io.reactivestax.active.life.canada.controller;

import io.reactivestax.active.life.canada.constant.Endpoints;
import io.reactivestax.active.life.canada.constant.Message;
import io.reactivestax.active.life.canada.constant.ShortConstant;
import io.reactivestax.active.life.canada.dto.CreateMemberRequest;
import io.reactivestax.active.life.canada.dto.MemberDetails;
import io.reactivestax.active.life.canada.dto.SuccessfulResponse;
import io.reactivestax.active.life.canada.dto.UpdateMemberRequest;
import io.reactivestax.active.life.canada.dto.group.CreateGroup;
import io.reactivestax.active.life.canada.model.SecurityHeader;
import io.reactivestax.active.life.canada.service.FamilyManagementService;
import io.reactivestax.active.life.canada.util.ActiveLifeUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(Endpoints.BASE_ENDPOINT + Endpoints.MEMBERS_BASE)
@RequiredArgsConstructor
public class FamilyManagementController {

    private final FamilyManagementService familyManagementService;
    private final ActiveLifeUtil activeLifeUtil;

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<SuccessfulResponse> addMember(@RequestHeader(name = ShortConstant.SECURITY_HEADER) String securityHeaderJson,
                                                        @Validated(CreateGroup.class) @RequestBody CreateMemberRequest createMemberRequest) {
        SecurityHeader securityHeader = activeLifeUtil.getSecurityHeader(securityHeaderJson);
        this.familyManagementService.createFamilyMember(createMemberRequest, securityHeader.getFamilyMemberId(), false);

        return ResponseEntity.ok(SuccessfulResponse.builder().message(Message.MEMBER_ADD_SUCCESSFUL).build());
    }

    @PatchMapping(value = Endpoints.MEMBER_ID, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<SuccessfulResponse> updateMember(@PathVariable String memberId,
                                                           @RequestHeader(name = ShortConstant.SECURITY_HEADER) String securityHeaderJson,
                                                           @Valid @RequestBody UpdateMemberRequest updateMemberRequest) {
        SecurityHeader securityHeader = activeLifeUtil.getSecurityHeader(securityHeaderJson);
        this.familyManagementService.updateFamilyMember(memberId, updateMemberRequest, securityHeader.getFamilyMemberId());

        return ResponseEntity.ok(SuccessfulResponse.builder().message(Message.MEMBER_UPDATED).build());
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @GetMapping(value = Endpoints.MEMBER_ID, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<MemberDetails> getMember(@PathVariable String memberId) {
//        SecurityHeader securityHeader = activeLifeUtil.getSecurityHeader(securityHeaderJson);
        MemberDetails memberDetails = this.familyManagementService.getFamilyMember(memberId, "example36");

        return ResponseEntity.ok(memberDetails);
    }

    @DeleteMapping(value = Endpoints.MEMBER_ID, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<SuccessfulResponse> deactivateMember(@PathVariable String memberId,
                                                               @RequestHeader(name = ShortConstant.SECURITY_HEADER) String securityHeaderJson) {
        SecurityHeader securityHeader = activeLifeUtil.getSecurityHeader(securityHeaderJson);
        this.familyManagementService.deactivateFamilyMember(memberId, securityHeader.getFamilyMemberId());

        return ResponseEntity.ok(SuccessfulResponse.builder().message(Message.MEMBER_DEACTIVATED).build());
    }
}
