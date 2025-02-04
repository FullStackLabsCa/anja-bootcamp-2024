package io.reactivestax.active.life.canada.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.reactivestax.active.life.canada.constant.ExceptionHandlerConst;
import io.reactivestax.active.life.canada.constant.ShortConstant;
import io.reactivestax.active.life.canada.dto.group.CreateGroup;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class BaseCourseDto {
    @JsonFormat(pattern = ShortConstant.DATE_PATTERN)
    private LocalDate startDate;

    @JsonFormat(pattern = ShortConstant.DATE_PATTERN)
    private LocalDate endDate;

    @NotNull(groups = CreateGroup.class, message = ExceptionHandlerConst.NULL_NO_OF_CLASSES)
    private Integer noOfClassesOffered;

    @JsonFormat(pattern = ShortConstant.TIME_PATTERN)
    private LocalTime startTime;

    @JsonFormat(pattern = ShortConstant.TIME_PATTERN)
    private LocalTime endTime;

    @NotNull(groups = CreateGroup.class, message = ExceptionHandlerConst.NULL_IS_ALL_DAY_COURSE)
    private Boolean isAllDayCourse;

    @JsonFormat(pattern = ShortConstant.DATE_PATTERN)
    private LocalDate registrationStartDate;

    @NotNull(groups = CreateGroup.class, message = ExceptionHandlerConst.NULL_NO_OF_SPOTS)
    private Integer noOfSpots;
}
