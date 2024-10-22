package io.reactivestax.entity;

import io.reactivestax.enums.Direction;
import io.reactivestax.enums.PostedStatus;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.sql.Timestamp;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Table(name = "journal_entry")
public class JournalEntry extends CustomTimestamp {

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
}
