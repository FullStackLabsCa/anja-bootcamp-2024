package io.reactivestax.active.life.canada.mapper;

import io.reactivestax.active.life.canada.constant.ShortConstant;
import io.reactivestax.active.life.canada.dto.CourseDto;
import io.reactivestax.active.life.canada.entity.Course;
import org.mapstruct.Mapper;

@Mapper(componentModel = ShortConstant.SPRING, uses = {SubCategoryMapper.class, AgeGroupMapper.class})
public interface CourseMapper {
    CourseDto toDto(Course course);
}
