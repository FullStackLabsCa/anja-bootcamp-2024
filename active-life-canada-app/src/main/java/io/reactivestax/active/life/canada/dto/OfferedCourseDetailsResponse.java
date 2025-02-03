package io.reactivestax.active.life.canada.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OfferedCourseDetailsResponse extends BaseCourseDto {
    private String barCode;
    private String availableForEnrollment;
    private List<CourseFeeDto> courseFee = new ArrayList<>();
    private CourseDto course;
    private FacilityDto facility;
}
