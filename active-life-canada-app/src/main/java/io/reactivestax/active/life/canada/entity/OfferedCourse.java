package io.reactivestax.active.life.canada.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class OfferedCourse extends AuditTrail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long offeredCourseId;

    private String barCode;
    private LocalDate startDate;
    private LocalDate endDate;
    private Short noOfClassesOffered;
    private LocalTime startTime;
    private LocalTime endTime;
    private Boolean isAllDayCourse;
    private LocalDate registrationStartDate;
    private String availableForEnrollment;

    @ManyToOne
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @ManyToOne
    @JoinColumn(name = "facility_id", nullable = false)
    private Facility facility;

    @OneToMany(mappedBy = "offeredCourse", cascade = CascadeType.ALL)
    private List<OfferedCourseFee> offeredCourseFees;
}
