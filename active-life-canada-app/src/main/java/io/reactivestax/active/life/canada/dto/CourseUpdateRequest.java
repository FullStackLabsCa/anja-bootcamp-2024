package io.reactivestax.active.life.canada.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.reactivestax.active.life.canada.constant.ExceptionHandlerConst;
import io.reactivestax.active.life.canada.enums.deserializer.AvailableForEnrollmentDeserializer;
import io.reactivestax.active.life.canada.enums.AvailableForEnrollment;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class CourseUpdateRequest extends BaseCourseDto {

    @NotEmpty(message = ExceptionHandlerConst.EMPTY_BAR_CODE)
    private String barCode;

    @JsonDeserialize(using = AvailableForEnrollmentDeserializer.class)
    private AvailableForEnrollment availableForEnrollment;

}
