package io.reactivestax.active.life.canada.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class OfferedCourseWaitlist extends AuditTrail {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID courseWaitListId;

    private UUID enrollmentActorId;

    @ToString.Exclude
    @ManyToOne
    @JoinColumn(name = "offered_course_id", nullable = false)
    private OfferedCourse offeredCourse;

    @ToString.Exclude
    @ManyToOne
    @JoinColumn(name = "family_member_id", nullable = false)
    private FamilyMember familyMember;
}
