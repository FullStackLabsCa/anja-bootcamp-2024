package io.reactivestax.active.life.canada.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import io.reactivestax.active.life.canada.enums.Gender;
import io.reactivestax.active.life.canada.enums.PreferredModeOfCommunication;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
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
    private String groupId;
    @Enumerated(value = EnumType.STRING)
    private PreferredModeOfCommunication preferredModeOfCommunication;
    private boolean isActive;
    private boolean isGroupAdmin;
    @ManyToOne
    @JoinColumn(name = "family_group_id", nullable = false)
    @JsonBackReference
    private FamilyGroup familyGroup;
    private String activationToken;

    @Override
    public String toString() {
        return "FamilyMember{" +
                "familyMemberId=" + familyMemberId +
                ", name='" + name + '\'' +
                ", dob=" + dob +
                ", gender=" + gender +
                ", emailId='" + emailId + '\'' +
                ", streetNo='" + streetNo + '\'' +
                ", streetName='" + streetName + '\'' +
                ", city='" + city + '\'' +
                ", province='" + province + '\'' +
                ", country='" + country + '\'' +
                ", homePhone='" + homePhone + '\'' +
                ", businessPhone='" + businessPhone + '\'' +
                ", language='" + language + '\'' +
                ", memberLoginId='" + memberLoginId + '\'' +
                ", groupId='" + groupId + '\'' +
                ", preferredModeOfCommunication=" + preferredModeOfCommunication +
                ", isActive=" + isActive +
                ", isGroupAdmin=" + isGroupAdmin +
                ", activationToken='" + activationToken + '\'' +
                '}';
    }
}
