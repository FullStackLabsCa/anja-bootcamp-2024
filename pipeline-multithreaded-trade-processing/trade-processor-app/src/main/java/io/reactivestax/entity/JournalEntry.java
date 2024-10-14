package io.reactivestax.entity;

import io.reactivestax.enums.DirectionEnum;
import io.reactivestax.enums.PostedStatusEnum;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "journal_entry")
public class JournalEntry {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "trade_id")
    private String tradeId;

    @Column(name = "account_number")
    private String accountNumber;

    @Column(name = "security_cusip")
    private String securityCusip;

    @Column(name = "direction")
    @Enumerated(EnumType.STRING)
    private DirectionEnum direction;

    @Column(name = "quantity")
    private int quantity;

    @Column(name = "posted_status")
    @Enumerated(EnumType.STRING)
    private PostedStatusEnum postedStatus = PostedStatusEnum.NOT_POSTED;

    @Column(name = "transaction_date_time")
    private Timestamp transactionDateTime;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
