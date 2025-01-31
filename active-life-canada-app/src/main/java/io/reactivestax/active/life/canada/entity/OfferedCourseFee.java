package io.reactivestax.active.life.canada.entity;

import io.reactivestax.active.life.canada.enums.FeeType;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class OfferedCourseFee extends AuditTrail {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID feeId;

    @Enumerated(value = EnumType.STRING)
    private FeeType feeType;
    private Integer courseFee;

    @ToString.Exclude
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "offered_course_id", nullable = false)
    private OfferedCourse offeredCourse;
}
