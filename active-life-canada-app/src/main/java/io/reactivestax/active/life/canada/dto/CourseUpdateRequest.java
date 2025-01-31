package io.reactivestax.active.life.canada.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.reactivestax.active.life.canada.dto.deserializer.AvailableForEnrollmentDeserializer;
import io.reactivestax.active.life.canada.enums.AvailableForEnrollment;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CourseUpdateRequest {
    private String barCode;
    private String startDate;
    private String endDate;
    private Integer noOfClassesOffered;
    private String startTime;
    private String endTime;
    private Boolean isAllDayCourse;
    private String registrationStartDate;
    @JsonDeserialize(using = AvailableForEnrollmentDeserializer.class)
    private AvailableForEnrollment availableForEnrollment;
    private Integer noOfSpots;
}
