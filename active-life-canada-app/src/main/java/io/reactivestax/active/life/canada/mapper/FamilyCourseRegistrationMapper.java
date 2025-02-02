package io.reactivestax.active.life.canada.mapper;

import io.reactivestax.active.life.canada.constant.ShortConstant;
import io.reactivestax.active.life.canada.dto.FamilyCourseRegistrationDetails;
import io.reactivestax.active.life.canada.entity.FamilyCourseRegistration;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = ShortConstant.SPRING, uses = {OfferedCourseMapper.class, FamilyMemberMapper.class})
public interface FamilyCourseRegistrationMapper {
    FamilyCourseRegistrationDetails toDto(FamilyCourseRegistration familyCourseRegistration);

    List<FamilyCourseRegistrationDetails> toDtoList(List<FamilyCourseRegistration> entities);
}