package io.reactivestax.active.life.canada.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OfferedCourseWaitlistDto {
    private String courseWaitListId;
    private String enrollmentActorId;
    private OfferedCourseDetailsResponse offeredCourse;
    private MemberDetails familyMember;
}
