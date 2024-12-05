package io.reactivestax.repository.hibernate.entity;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@Embeddable
public class PositionCompositeKey implements Serializable {
    private String accountNumber;
    private String securityCusip;
}
