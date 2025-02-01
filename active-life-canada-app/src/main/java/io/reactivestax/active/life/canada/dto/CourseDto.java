package io.reactivestax.active.life.canada.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class CourseDto extends NameDescription {
    private Long courseId;
    private SubCategoryDto subCategory;
    private AgeGroupDto ageGroup;
}
