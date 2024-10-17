package io.reactivestax.entity;

import io.reactivestax.enums.PostedStatusEnum;
import io.reactivestax.enums.LookupStatusEnum;
import io.reactivestax.enums.ValidityStatusEnum;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.sql.Timestamp;

@Data
@Entity
@Table(name = "trade_payloads")
public class TradePayload {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "trade_number", nullable = false, unique = true)
    private String tradeNumber;

    @Column(name = "payload", nullable = false)
    private String payload;

    @Column(name = "validity_status", nullable = false)
    @Enumerated(EnumType.STRING)
    private ValidityStatusEnum validityStatus = ValidityStatusEnum.VALID;

    @Column(name = "lookup_status", nullable = false)
    @Enumerated(EnumType.STRING)
    private LookupStatusEnum lookupStatus = LookupStatusEnum.NOT_CHECKED;

    @Column(name = "je_status", nullable = false)
    @Enumerated(EnumType.STRING)
    private PostedStatusEnum journalEntryStatus = PostedStatusEnum.NOT_POSTED;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false, nullable = false)
    private Timestamp createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private Timestamp updatedAt;
}
