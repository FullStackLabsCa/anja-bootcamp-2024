package io.reactivestax.active.life.canada.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.reactivestax.active.life.canada.constant.Endpoints;
import io.reactivestax.active.life.canada.dto.CreateMemberRequest;
import io.reactivestax.active.life.canada.model.SecurityHeader;
import io.reactivestax.active.life.canada.service.FamilyManagementService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(Endpoints.BASE_URL + Endpoints.MEMBERS_BASE)
public class FamilyManagementController {

    private final FamilyManagementService familyManagementService;

    public FamilyManagementController(FamilyManagementService familyManagementService) {
        this.familyManagementService = familyManagementService;
    }

    @PostMapping()
    public void addMember(@RequestHeader(name = "X-security-header") String securityHeaderJson,
                          @RequestBody CreateMemberRequest createMemberRequest) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        SecurityHeader securityHeader = objectMapper.readValue(securityHeaderJson, SecurityHeader.class);
        this.familyManagementService.createFamilyMember(createMemberRequest, securityHeader, false);
    }
}
