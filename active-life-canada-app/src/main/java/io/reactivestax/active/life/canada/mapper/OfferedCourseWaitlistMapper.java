package io.reactivestax.active.life.canada.mapper;

import io.reactivestax.active.life.canada.constant.ShortConstant;
import io.reactivestax.active.life.canada.dto.OfferedCourseWaitlistDto;
import io.reactivestax.active.life.canada.entity.OfferedCourseWaitlist;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = ShortConstant.SPRING, uses = {OfferedCourseMapper.class, FamilyMemberMapper.class})
public interface OfferedCourseWaitlistMapper {

    OfferedCourseWaitlistDto toDto(OfferedCourseWaitlist offeredCourseWaitlist);

    List<OfferedCourseWaitlistDto> toDtoList(List<OfferedCourseWaitlist> offeredCourseWaitlist);
}
