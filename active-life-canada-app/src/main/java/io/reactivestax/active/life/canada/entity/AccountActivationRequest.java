package io.reactivestax.active.life.canada.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class AccountActivationRequest extends AuditTrail {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID accountActivationRequestId;

    private UUID token;

    private UUID familyMemberId;
}
