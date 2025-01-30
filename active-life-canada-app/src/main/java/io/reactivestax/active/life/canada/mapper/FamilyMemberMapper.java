package io.reactivestax.active.life.canada.mapper;

import io.reactivestax.active.life.canada.entity.FamilyMember;
import io.reactivestax.active.life.canada.dto.CreateMemberRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface FamilyMemberMapper {
    FamilyMemberMapper INSTANCE = Mappers.getMapper(FamilyMemberMapper.class);

    @Mapping(target = "isActive", constant = "false")
    @Mapping(source = "username", target = "memberLoginId")
    FamilyMember registerMemberRequestToFamilyMember(CreateMemberRequest createMemberRequest);
}
