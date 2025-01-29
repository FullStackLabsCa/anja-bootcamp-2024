package io.reactivestax.active.life.canada.domain;

import io.reactivestax.active.life.canada.enums.Gender;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;

@Entity
@Builder
@Data
public class FamilyMember {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID memberId;
    private String name;
    private Date dob;
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
    private String memberLoginId;
    private String groupId;
    private Long credits = 0L;
    private Long failedLoginAttempts = 0L;
    @CreationTimestamp
    private LocalDateTime createdAt;
    private LocalDateTime createdBy;
    @UpdateTimestamp
    private LocalDateTime updatedAt;
    private LocalDateTime updatedBy;
}
