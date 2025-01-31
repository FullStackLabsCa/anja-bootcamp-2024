package io.reactivestax.active.life.canada.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@EntityListeners(AuditingEntityListener.class)
public class FamilyCourseRegistration extends AuditTrail {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID familyCourseRegistrationId;

    private Integer cost;
    @CreatedDate
    @Column(updatable = false, nullable = false)
    private LocalDate enrollmentDate;
    private Boolean isWithdrawn;
    private Integer withdrawnCredits;
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
