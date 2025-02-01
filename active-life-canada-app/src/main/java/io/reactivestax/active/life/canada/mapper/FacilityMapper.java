package io.reactivestax.active.life.canada.mapper;

import io.reactivestax.active.life.canada.constant.ShortConstant;
import io.reactivestax.active.life.canada.dto.FacilityDto;
import io.reactivestax.active.life.canada.entity.Facility;
import org.mapstruct.Mapper;

@Mapper(componentModel = ShortConstant.SPRING)
public interface FacilityMapper {
    FacilityDto toDto(Facility facility);
}
