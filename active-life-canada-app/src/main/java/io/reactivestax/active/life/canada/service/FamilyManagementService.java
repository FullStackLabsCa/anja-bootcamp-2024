package io.reactivestax.active.life.canada.service;

import io.reactivestax.active.life.canada.domain.FamilyGroup;
import io.reactivestax.active.life.canada.domain.FamilyMember;
import io.reactivestax.active.life.canada.dto.RegisterMemberRequest;
import io.reactivestax.active.life.canada.repository.FamilyMemberRepository;
import org.springframework.stereotype.Service;

@Service
public class FamilyManagementService {

    private final FamilyMemberRepository familyMemberRepository;
    private final FamilyGroupRepository familyGroupRepository;

    public FamilyManagementService(FamilyMemberRepository familyMemberRepository,
                                   FamilyGroupRepository familyGroupRepository) {
        this.familyMemberRepository = familyMemberRepository;
        this.familyGroupRepository = familyGroupRepository;
    }

    public void createFamilyMember(RegisterMemberRequest registerMemberRequest, boolean isGroupAdmin) {
        FamilyGroup familyGroup = convertToEntity(registerMemberRequest, isGroupAdmin);
        FamilyGroup saved = familyGroupRepository.save(familyGroup);
        System.out.println(saved.getFamilyGroupId());
    }

    private FamilyGroup convertToEntity(RegisterMemberRequest registerMemberRequest, boolean isGroupAdmin) {
        FamilyMember familyMember = FamilyMember.builder()
                .name(registerMemberRequest.getName())
                .dob(registerMemberRequest.getDob())
                .gender(registerMemberRequest.getGender())
                .emailId(registerMemberRequest.getEmailId())
                .streetNo(registerMemberRequest.getStreetNo())
                .streetName(registerMemberRequest.getStreetName())
                .city(registerMemberRequest.getCity())
                .province(registerMemberRequest.getProvince())
                .country(registerMemberRequest.getCountry())
                .homePhone(registerMemberRequest.getHomePhone())
                .businessPhone(registerMemberRequest.getBusinessPhone())
                .language(registerMemberRequest.getLanguage())
                .memberLoginId(registerMemberRequest.getUsername())
                .preferredModeOfCommunication(registerMemberRequest.getPreferredModeOfCommunication())
                .isActive(false)
                .isGroupAdmin(isGroupAdmin)
                .build();
        FamilyGroup familyGroup = familyMember.getFamilyGroup();
        if (isGroupAdmin) {
            familyGroup = new FamilyGroup();
            familyGroup.setFamilyPin(registerMemberRequest.getPin());
            familyMember.setFamilyGroup(familyGroup);
        }
        familyGroup.getFamilyMembers().add(familyMember);
        return familyGroup;
    }
}
