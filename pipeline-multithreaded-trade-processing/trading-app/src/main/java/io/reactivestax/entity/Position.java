package io.reactivestax.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Table(name = "positions")
public class Position extends CustomTimestamp {
    @EmbeddedId
    @AttributeOverride(name = "accountNumber", column = @Column(name = "account_number"))
    @AttributeOverride(name = "securityCusip", column = @Column(name = "security_cusip"))
    private PositionCompositeKey positionCompositeKey;

    @Column(name = "holding", nullable = false)
    private long holding;

    @Version
    @Column(name = "version", nullable = false)
    private int version;
}
