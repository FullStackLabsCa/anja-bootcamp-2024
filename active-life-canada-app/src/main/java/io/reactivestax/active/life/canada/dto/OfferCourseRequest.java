package io.reactivestax.active.life.canada.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.reactivestax.active.life.canada.constant.ShortConstant;
import io.reactivestax.active.life.canada.dto.deserializer.FeeTypeDeserializer;
import io.reactivestax.active.life.canada.enums.FeeType;
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
    @JsonDeserialize(using = FeeTypeDeserializer.class)
    private FeeType feeType;
    private Integer courseFee;
}
