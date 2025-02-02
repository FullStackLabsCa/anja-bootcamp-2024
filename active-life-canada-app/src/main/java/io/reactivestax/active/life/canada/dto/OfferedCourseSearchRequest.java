package io.reactivestax.active.life.canada.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OfferedCourseSearchRequest {
    private String courseName;
    private LocalDate startDate;
    private LocalDate endDate;
    private String city;
    private String province;
    private String category;
    private String subCategory;
    private String ageGroup;
}
