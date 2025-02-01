package io.reactivestax.active.life.canada.entity;

import io.reactivestax.active.life.canada.enums.Gender;
import io.reactivestax.active.life.canada.enums.PreferredModeOfCommunication;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
@Entity
@SuperBuilder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FamilyMember extends AuditTrail {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID familyMemberId;
    private String name;
    private LocalDate dob;
    @Enumerated(value = EnumType.STRING)
    private Gender gender;
    private String emailId;
    private String streetNo;
    private String streetName;
    private String city;
    private String province;
    private String country;
    private String homePhone;
    private String businessPhone;
    private String language;
    @Column(unique = true, nullable = false)
    private String memberLoginId;
    @Enumerated(value = EnumType.STRING)
    private PreferredModeOfCommunication preferredModeOfCommunication;
    private boolean isActive;
    private boolean isGroupAdmin;

    @ManyToOne
    @JoinColumn(name = "family_group_id", nullable = false)
    @ToString.Exclude
    private FamilyGroup familyGroup;

    private String activationToken;

    @ToString.Exclude
    @OneToMany(mappedBy = "familyMember", cascade = CascadeType.ALL)
    private List<OfferedCourseWaitlist> offeredCourseWaitlist = new ArrayList<>();
}
