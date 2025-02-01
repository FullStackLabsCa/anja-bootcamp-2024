package io.reactivestax.active.life.canada.mapper;

import io.reactivestax.active.life.canada.constant.ShortConstant;
import io.reactivestax.active.life.canada.dto.AgeGroupDto;
import io.reactivestax.active.life.canada.entity.AgeGroup;
import org.mapstruct.Mapper;

@Mapper(componentModel = ShortConstant.SPRING)
public interface AgeGroupMapper {

    AgeGroupDto toDto(AgeGroup ageGroup);

}
