package io.reactivestax.active.life.canada.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class OfferedCourseFee extends AuditTrail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long feeId;

    private String feeType;
    private BigDecimal courseFee;

    @ManyToOne
    @JoinColumn(name = "offered_course_id", nullable = false)
    private OfferedCourse offeredCourse;
}
