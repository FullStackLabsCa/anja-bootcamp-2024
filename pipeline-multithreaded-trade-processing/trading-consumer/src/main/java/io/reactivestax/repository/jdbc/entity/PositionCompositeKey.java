package io.reactivestax.repository.jdbc.entity;

import lombok.Data;

@Data
public class PositionCompositeKey {
    private String accountNumber;
    private String securityCusip;
}
