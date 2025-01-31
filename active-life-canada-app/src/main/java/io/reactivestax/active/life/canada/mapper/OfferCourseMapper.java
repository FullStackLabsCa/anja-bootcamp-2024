package io.reactivestax.active.life.canada.mapper;

import io.reactivestax.active.life.canada.dto.OfferCourseRequest;
import io.reactivestax.active.life.canada.entity.OfferedCourse;
import io.reactivestax.active.life.canada.entity.OfferedCourseFee;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface OfferCourseMapper {
    OfferedCourse offerCourseRequestToOfferedCourse(OfferCourseRequest offerCourseRequest);

    OfferedCourseFee offerCourseRequestToOfferedCourseFee(OfferCourseRequest offerCourseRequest);
}
