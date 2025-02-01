package io.reactivestax.active.life.canada.mapper;

import io.reactivestax.active.life.canada.constant.ShortConstant;
import io.reactivestax.active.life.canada.dto.CategoryDto;
import io.reactivestax.active.life.canada.entity.Category;
import org.mapstruct.Mapper;

@Mapper(componentModel = ShortConstant.SPRING)
public interface CategoryMapper {
    CategoryDto toDto(Category course);
}
