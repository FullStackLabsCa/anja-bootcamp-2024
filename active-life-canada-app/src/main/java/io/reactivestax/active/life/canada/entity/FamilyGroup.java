package io.reactivestax.active.life.canada.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
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
    @JsonManagedReference
    @OneToMany(mappedBy = "familyMemberId", cascade = CascadeType.ALL)
    private List<FamilyMember> familyMembers = new ArrayList<>();

    @Override
    public String toString() {
        return "FamilyGroup{" +
                "failedLoginAttempts=" + failedLoginAttempts +
                ", status=" + status +
                ", credits=" + credits +
                ", familyPin='" + familyPin + '\'' +
                ", familyGroupId=" + familyGroupId +
                '}';
    }
}
