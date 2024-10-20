package io.reactivestax.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.sql.Timestamp;

@Data
@Entity
@Table(name = "positions")
public class Position {
    @EmbeddedId
    @AttributeOverride(name = "accountNumber", column = @Column(name = "account_number"))
    @AttributeOverride(name = "securityCusip", column = @Column(name = "security_cusip"))
    private PositionCompositeKey positionCompositeKey;

    @Column(name = "holding", nullable = false)
    private int holding;    

    @Version
    @Column(name = "version", nullable = false)
    private int version;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false, nullable = false)
    private Timestamp createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private Timestamp updatedAt;
}
