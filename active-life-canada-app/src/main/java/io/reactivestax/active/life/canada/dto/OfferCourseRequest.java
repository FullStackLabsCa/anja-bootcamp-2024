package io.reactivestax.active.life.canada.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.reactivestax.active.life.canada.constant.ShortConstant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OfferCourseRequest {
    private Long courseId;
    private Long facilityId;
    @JsonFormat(pattern = ShortConstant.DATE_PATTERN)
    private LocalDate startDate;
    @JsonFormat(pattern = ShortConstant.DATE_PATTERN)
    private LocalDate endDate;
    private Integer noOfClassesOffered;
    @JsonFormat(pattern = ShortConstant.TIME_PATTERN)
    private LocalTime startTime;
    @JsonFormat(pattern = ShortConstant.TIME_PATTERN)
    private LocalTime endTime;
    private Boolean isAllDayCourse;
    @JsonFormat(pattern = ShortConstant.DATE_PATTERN)
    private LocalDate registrationStartDate;
    private Integer noOfSpots;
    private Integer residentCourseFee;
    private Integer nonResidentCourseFee;
}
