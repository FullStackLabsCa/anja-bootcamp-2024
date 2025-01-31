package io.reactivestax.active.life.canada.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CourseDetailsResponse {
    private String barCode;
    private String startDate;
    private String endDate;
    private Integer noOfClassesOffered;
    private String startTime;
    private String endTime;
    private Boolean isAllDayCourse;
    private String registrationStartDate;
    private String availableForEnrollment;
    private List<CourseFeeDto> courseFee = new ArrayList<>();
}
