package io.reactivestax.active.life.canada.service;

import io.reactivestax.active.life.canada.domain.FamilyMember;
import io.reactivestax.active.life.canada.dto.RegisterMemberRequest;
import io.reactivestax.active.life.canada.enums.Gender;
import io.reactivestax.active.life.canada.repository.FamilyMemberRepository;
import org.springframework.stereotype.Service;

import java.sql.Date;

@Service
public class FamilyMemberService {

    private final FamilyMemberRepository familyMemberRepository;

    public FamilyMemberService(FamilyMemberRepository familyMemberRepository){
        this.familyMemberRepository = familyMemberRepository;
    }

    public void createFamilyMember(RegisterMemberRequest registerMemberRequest){

    }

    private FamilyMember convertToEntity(RegisterMemberRequest registerMemberRequest){
        return FamilyMember.builder()
                .name(registerMemberRequest.getName())
                .dob(Date.valueOf(registerMemberRequest.getDob()))
                .gender(registerMemberRequest.getGender())
                .build();
    }
}
