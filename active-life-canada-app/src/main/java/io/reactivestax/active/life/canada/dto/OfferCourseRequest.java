package io.reactivestax.active.life.canada.dto;


import io.reactivestax.active.life.canada.constant.ExceptionHandlerConst;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class OfferCourseRequest extends BaseCourseDto {
    @NotNull(message = ExceptionHandlerConst.EMPTY_COURSE_ID)
    private Long courseId;

    @NotNull(message = ExceptionHandlerConst.EMPTY_FACILITY_ID)
    private Long facilityId;

    @NotNull(message = ExceptionHandlerConst.EMPTY_RESIDENT_FEE)
    private Integer residentCourseFee;

    @NotNull(message = ExceptionHandlerConst.EMPTY_NON_RESIDENT_FEE)
    private Integer nonResidentCourseFee;
}
