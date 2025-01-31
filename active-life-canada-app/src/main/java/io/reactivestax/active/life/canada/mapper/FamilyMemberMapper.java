package io.reactivestax.active.life.canada.mapper;

import io.reactivestax.active.life.canada.constant.ShortConstant;
import io.reactivestax.active.life.canada.dto.CreateMemberRequest;
import io.reactivestax.active.life.canada.dto.MemberDetails;
import io.reactivestax.active.life.canada.dto.UpdateMemberRequest;
import io.reactivestax.active.life.canada.entity.FamilyMember;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = ShortConstant.SPRING)
public interface FamilyMemberMapper {
    FamilyMemberMapper INSTANCE = Mappers.getMapper(FamilyMemberMapper.class);

    @Mapping(target = ShortConstant.IS_ACTIVE, constant = ShortConstant.FALSE)
    @Mapping(source = ShortConstant.USERNAME, target = ShortConstant.MEMBER_LOGIN_ID)
    FamilyMember registerMemberRequestToFamilyMember(CreateMemberRequest createMemberRequest);


    @Mapping(source = ShortConstant.MEMBER_LOGIN_ID, target = ShortConstant.USERNAME)
    MemberDetails familyMemberToMemberDetails(FamilyMember familyMember);

    @Mapping(source = ShortConstant.USERNAME, target = ShortConstant.MEMBER_LOGIN_ID)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateMemberRequestToFamilyMember(UpdateMemberRequest updateMemberRequest,
                                           @MappingTarget FamilyMember familyMember);
}
