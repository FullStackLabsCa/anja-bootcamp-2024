package io.reactivestax.active.life.canada.mapper;

import io.reactivestax.active.life.canada.dto.CreateMemberRequest;
import io.reactivestax.active.life.canada.dto.MemberDetails;
import io.reactivestax.active.life.canada.entity.FamilyMember;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface FamilyMemberMapper {
    FamilyMemberMapper INSTANCE = Mappers.getMapper(FamilyMemberMapper.class);

    @Mapping(target = "isActive", constant = "false")
    @Mapping(source = "username", target = "memberLoginId")
    FamilyMember registerMemberRequestToFamilyMember(CreateMemberRequest createMemberRequest);


    @Mapping(source = "memberLoginId", target = "username")
    MemberDetails familyMemberToMemberDetails(FamilyMember familyMember);
}
