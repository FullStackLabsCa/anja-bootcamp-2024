package io.reactivestax.active.life.canada.domain;

import io.reactivestax.active.life.canada.enums.Status;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class FamilyGroup extends AuditTrail {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID familyGroupId;
    private String familyPin;
    private Double credits = 0.0;
    @Enumerated(value = EnumType.STRING)
    private Status status = Status.INACTIVE;
    private Integer failedLoginAttempts = 0;
    @OneToMany(mappedBy = "familyMemberId", cascade = CascadeType.ALL)
    private List<FamilyMember> familyMembers = new ArrayList<>();
}
