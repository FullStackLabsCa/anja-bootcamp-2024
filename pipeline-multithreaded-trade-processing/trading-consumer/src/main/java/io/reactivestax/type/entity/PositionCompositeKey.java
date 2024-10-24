package io.reactivestax.type.entity;

import jakarta.persistence.Embeddable;
import lombok.Data;

import java.io.Serializable;

@Data
@Embeddable
public class PositionCompositeKey implements Serializable {
    private String accountNumber;
    private String securityCusip;
}
