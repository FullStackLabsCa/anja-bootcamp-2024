package io.reactivestax.active.life.canada.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.reactivestax.active.life.canada.constant.Endpoints;
import io.reactivestax.active.life.canada.constant.Message;
import io.reactivestax.active.life.canada.dto.CreateMemberRequest;
import io.reactivestax.active.life.canada.dto.MemberDetails;
import io.reactivestax.active.life.canada.dto.SuccessfulResponse;
import io.reactivestax.active.life.canada.dto.UpdateMemberRequest;
import io.reactivestax.active.life.canada.model.SecurityHeader;
import io.reactivestax.active.life.canada.service.FamilyManagementService;
import io.reactivestax.active.life.canada.util.ActiveLifeUtil;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(Endpoints.BASE_ENDPOINT + Endpoints.MEMBERS_BASE)
public class FamilyManagementController {

    private final FamilyManagementService familyManagementService;
    private final ActiveLifeUtil activeLifeUtil;

    public FamilyManagementController(FamilyManagementService familyManagementService,
                                      ActiveLifeUtil activeLifeUtil) {
        this.familyManagementService = familyManagementService;
        this.activeLifeUtil = activeLifeUtil;
    }

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<SuccessfulResponse> addMember(@RequestHeader(name = "X-security-header") String securityHeaderJson,
                                                        @RequestBody CreateMemberRequest createMemberRequest) throws JsonProcessingException {
        SecurityHeader securityHeader = activeLifeUtil.getSecurityHeader(securityHeaderJson);
        this.familyManagementService.createFamilyMember(createMemberRequest, securityHeader.getFamilyMemberId(), false);

        return ResponseEntity.ok(SuccessfulResponse.builder().message(Message.MEMBER_ADD_SUCCESSFUL).build());
    }

    @PatchMapping(value = Endpoints.MEMBER_ID, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<SuccessfulResponse> updateMember(@PathVariable String memberId,
                                                           @RequestHeader(name = "X-security-header") String securityHeaderJson,
                                                           @RequestBody UpdateMemberRequest updateMemberRequest) {
        SecurityHeader securityHeader = activeLifeUtil.getSecurityHeader(securityHeaderJson);
        this.familyManagementService.updateFamilyMember(memberId, updateMemberRequest, securityHeader.getFamilyMemberId());

        return ResponseEntity.ok(SuccessfulResponse.builder().message(Message.MEMBER_UPDATED).build());
    }

    @GetMapping(value = Endpoints.MEMBER_ID, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<MemberDetails> getMember(@PathVariable String memberId,
                                                   @RequestHeader(name = "X-security-header") String securityHeaderJson) {
        SecurityHeader securityHeader = activeLifeUtil.getSecurityHeader(securityHeaderJson);
        MemberDetails memberDetails = this.familyManagementService.getFamilyMember(memberId, securityHeader.getFamilyMemberId());

        return ResponseEntity.ok(memberDetails);
    }

    @DeleteMapping(value = Endpoints.MEMBER_ID, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<SuccessfulResponse> deactivateMember(@PathVariable String memberId,
                                                               @RequestHeader(name = "X-security-header") String securityHeaderJson) {
        SecurityHeader securityHeader = activeLifeUtil.getSecurityHeader(securityHeaderJson);
        this.familyManagementService.deactivateFamilyMember(memberId, securityHeader.getFamilyMemberId());

        return ResponseEntity.ok(SuccessfulResponse.builder().message(Message.MEMBER_DEACTIVATED).build());
    }
}
