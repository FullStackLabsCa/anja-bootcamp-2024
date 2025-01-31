package io.reactivestax.active.life.canada.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class FamilyCourseRegistration extends AuditTrail{

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID familyCourseRegistrationId;

    private String cost;
    private LocalDate enrollmentDate;
    private Boolean isWithdrawn;
    private Short withdrawnCredits;
    private String withdrawActor;
    private UUID enrollmentActorId;

    @ManyToOne
    @JoinColumn(name = "offered_course_id", nullable = false)
    private OfferedCourse offeredCourse;

    @ManyToOne
    @JoinColumn(name = "family_member_id", nullable = false)
    private FamilyMember familyMember;
}
