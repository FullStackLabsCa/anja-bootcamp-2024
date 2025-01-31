package io.reactivestax.active.life.canada.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OfferCourseRequest {
    private String courseId;
    private String facilityId;
    private String startDate;
    private String endDate;
    private Integer noOfClassesOffered;
    private String startTime;
    private String endTime;
    private Boolean isAllDayCourse;
    private String registrationStartDate;
    private String feeType;
    private Integer courseFee;
}
