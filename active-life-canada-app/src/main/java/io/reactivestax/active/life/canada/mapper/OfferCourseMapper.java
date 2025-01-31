package io.reactivestax.active.life.canada.mapper;

import io.reactivestax.active.life.canada.constant.ShortConstant;
import io.reactivestax.active.life.canada.dto.CourseDetailsResponse;
import io.reactivestax.active.life.canada.dto.CourseFeeDto;
import io.reactivestax.active.life.canada.dto.CourseUpdateRequest;
import io.reactivestax.active.life.canada.dto.OfferCourseRequest;
import io.reactivestax.active.life.canada.entity.OfferedCourse;
import io.reactivestax.active.life.canada.entity.OfferedCourseFee;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = ShortConstant.SPRING)
public interface OfferCourseMapper {
    @Mapping(target = ShortConstant.AVAILABLE_FOR_ENROLLMENT, constant = "AVAILABLE")
    OfferedCourse offerCourseRequestToOfferedCourse(OfferCourseRequest offerCourseRequest);

    @Mapping(source = "offeredCourseFees", target = "courseFee")
    @Mapping(target = "barCode", expression = "java(offeredCourse.getBarCode().toString())")
    CourseDetailsResponse toCourseDetailsResponse(OfferedCourse offeredCourse);

    @Mapping(target = "feeId", expression = "java(offeredCourseFee.getFeeId().toString())")
    CourseFeeDto toCourseFeeDto(OfferedCourseFee offeredCourseFee);

    List<CourseFeeDto> toCourseFeeDtoList(List<OfferedCourseFee> offeredCourseFees);

    List<CourseDetailsResponse> offeredCoursesToListOfCourseDetails(List<OfferedCourse> offeredCourses);

    OfferedCourseFee offerCourseRequestToOfferedCourseFee(OfferCourseRequest offerCourseRequest);

    @Mapping(target = ShortConstant.BAR_CODE, ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateOfferedCourseRequestToOfferedCourse(CourseUpdateRequest courseUpdateRequest,
                                                   @MappingTarget OfferedCourse offeredCourse);
}
