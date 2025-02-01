package io.reactivestax.active.life.canada.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DashboardDto {
    private List<FamilyCourseRegistrationDetails> registeredCourses;
    private List<OfferedCourseWaitlistDto> waitlistedCourses;
}
