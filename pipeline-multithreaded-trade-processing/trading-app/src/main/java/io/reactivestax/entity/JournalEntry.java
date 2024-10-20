package io.reactivestax.entity;

import io.reactivestax.enums.DirectionEnum;
import io.reactivestax.enums.PostedStatusEnum;
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
    private int id;

    @Column(name = "trade_id", nullable = false, unique = true)
    private String tradeId;

    @Column(name = "account_number", nullable = false)
    private String accountNumber;

    @Column(name = "security_cusip", nullable = false)
    private String securityCusip;

    @Column(name = "direction", nullable = false)
    @Enumerated(EnumType.STRING)
    private DirectionEnum direction;

    @Column(name = "quantity", nullable = false)
    private int quantity;

    @Column(name = "posted_status", nullable = false)
    @Enumerated(EnumType.STRING)
    private PostedStatusEnum postedStatus = PostedStatusEnum.NOT_POSTED;

    @Column(name = "transaction_date_time", nullable = false)
    private Timestamp transactionDateTime;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false, nullable = false)
    private Timestamp createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private Timestamp updatedAt;
}
