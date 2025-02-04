package io.reactivestax.active.life.canada.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FamilyCourseRegistrationDetails {
    private UUID familyCourseRegistrationId;
    private Integer cost;
    private LocalDate enrollmentDate;
    private Boolean isWithdrawn;
    private Integer withdrawnCredits;
    private UUID enrollmentActorId;
    private OfferedCourseDetailsResponse offeredCourse;
    private MemberDetails familyMember;
}
