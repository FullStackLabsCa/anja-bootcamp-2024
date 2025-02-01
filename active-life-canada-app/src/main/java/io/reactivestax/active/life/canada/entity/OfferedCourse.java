package io.reactivestax.active.life.canada.entity;

import io.reactivestax.active.life.canada.enums.AvailableForEnrollment;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class OfferedCourse extends AuditTrail {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID offeredCourseId;

    @Column(unique = true, nullable = false)
    private UUID barCode;
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer noOfClassesOffered;
    private LocalTime startTime;
    private LocalTime endTime;
    private Boolean isAllDayCourse;
    private LocalDate registrationStartDate;
    private Integer noOfSpots;
    @Enumerated(EnumType.STRING)
    private AvailableForEnrollment availableForEnrollment = AvailableForEnrollment.AVAILABLE;

    @ToString.Exclude
    @ManyToOne
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @ToString.Exclude
    @ManyToOne
    @JoinColumn(name = "facility_id", nullable = false)
    private Facility facility;

    @ToString.Exclude
    @OneToMany(mappedBy = "offeredCourse", cascade = CascadeType.ALL)
    private List<FamilyCourseRegistration> familyCourseRegistrations = new ArrayList<>();

    @ToString.Exclude
    @OneToMany(mappedBy = "offeredCourse", cascade = CascadeType.ALL)
    private List<OfferedCourseFee> offeredCourseFees = new ArrayList<>();

    @ToString.Exclude
    @OneToMany(mappedBy = "offeredCourse", cascade = CascadeType.ALL)
    private List<OfferedCourseWaitlist> offeredCourseWaitlist = new ArrayList<>();
}
