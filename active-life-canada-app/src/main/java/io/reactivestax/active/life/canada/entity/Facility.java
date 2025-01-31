package io.reactivestax.active.life.canada.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Facility extends AuditTrail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long facilityId;

    private String name;
    private String streetNo;
    private String streetName;
    private String city;
    private String province;
    private String country;
    private String postalCode;
    private String description;

    @ToString.Exclude
    @OneToMany(mappedBy = "facility", cascade = CascadeType.ALL)
    private List<OfferedCourse> offeredCourses;

    @ToString.Exclude
    @OneToMany(mappedBy = "facility", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FacilityFunction> facilityFunctions = new ArrayList<>();
}
