package io.reactivestax.active.life.canada.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.reactivestax.active.life.canada.constant.Endpoints;
import io.reactivestax.active.life.canada.constant.Message;
import io.reactivestax.active.life.canada.dto.CreateMemberRequest;
import io.reactivestax.active.life.canada.dto.SuccessfulResponse;
import io.reactivestax.active.life.canada.model.SecurityHeader;
import io.reactivestax.active.life.canada.service.FamilyManagementService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(Endpoints.BASE_URL + Endpoints.MEMBERS_BASE)
public class FamilyManagementController {

    private final FamilyManagementService familyManagementService;

    public FamilyManagementController(FamilyManagementService familyManagementService) {
        this.familyManagementService = familyManagementService;
    }

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<SuccessfulResponse> addMember(@RequestHeader(name = "X-security-header") String securityHeaderJson,
                                                        @RequestBody CreateMemberRequest createMemberRequest) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        SecurityHeader securityHeader = objectMapper.readValue(securityHeaderJson, SecurityHeader.class);
        this.familyManagementService.createFamilyMember(createMemberRequest, securityHeader, false);

        return ResponseEntity.ok(SuccessfulResponse.builder().message(Message.MEMBER_ADD_SUCCESSFUL).build());
    }

    @DeleteMapping(value = Endpoints.MEMBER_ID, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<SuccessfulResponse> deactivateMember(@PathVariable String memberId){
        this.familyManagementService.deactivateFamilyMember(memberId);

        return ResponseEntity.ok(SuccessfulResponse.builder().message(Message.MEMBER_DEACTIVATED).build());
    }
}
