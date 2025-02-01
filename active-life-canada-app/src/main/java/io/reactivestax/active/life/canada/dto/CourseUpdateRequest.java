package io.reactivestax.active.life.canada.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.reactivestax.active.life.canada.dto.deserializer.AvailableForEnrollmentDeserializer;
import io.reactivestax.active.life.canada.enums.AvailableForEnrollment;
import lombok.*;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class CourseUpdateRequest extends BaseCourseDto {
    private String barCode;
    @JsonDeserialize(using = AvailableForEnrollmentDeserializer.class)
    private AvailableForEnrollment availableForEnrollment;
}
