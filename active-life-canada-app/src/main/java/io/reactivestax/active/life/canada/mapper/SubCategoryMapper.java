package io.reactivestax.active.life.canada.mapper;

import io.reactivestax.active.life.canada.constant.ShortConstant;
import io.reactivestax.active.life.canada.dto.SubCategoryDto;
import io.reactivestax.active.life.canada.entity.SubCategory;
import org.mapstruct.Mapper;

@Mapper(componentModel = ShortConstant.SPRING, uses = CategoryMapper.class)
public interface SubCategoryMapper {
    SubCategoryDto toDto(SubCategory subCategory);
}
