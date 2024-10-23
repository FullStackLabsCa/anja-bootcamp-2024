package io.reactivestax.consumer.type.entity;

import io.reactivestax.consumer.type.enums.Direction;
import io.reactivestax.consumer.type.enums.PostedStatus;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.sql.Timestamp;

@Data
@Entity
@Table(name = "journal_entry")
public class JournalEntry {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "trade_id", nullable = false, unique = true)
    private String tradeId;

    @Column(name = "account_number", nullable = false)
    private String accountNumber;

    @Column(name = "security_cusip", nullable = false)
    private String securityCusip;

    @Column(name = "direction", nullable = false)
    @Enumerated(EnumType.STRING)
    private Direction direction;

    @Column(name = "quantity", nullable = false)
    private int quantity;

    @Column(name = "posted_status", nullable = false)
    @Enumerated(EnumType.STRING)
    private PostedStatus postedStatus = PostedStatus.NOT_POSTED;

    @Column(name = "transaction_timestamp", nullable = false)
    private Timestamp transactionTimestamp;

    @CreationTimestamp
    @Column(name = "created_timestamp", updatable = false, nullable = false)
    private Timestamp createdTimestamp;

    @UpdateTimestamp
    @Column(name = "updated_timestamp", nullable = false)
    private Timestamp updatedTimestamp;
}
