package io.reactivestax.entity;

import io.reactivestax.enums.PostedStatusEnum;
import io.reactivestax.enums.LookupStatusEnum;
import io.reactivestax.enums.ValidityStatusEnum;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "trade_payloads")
public class TradePayload {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "trade_number")
    private String tradeNumber;

    @Column(name = "payload")
    private String payload;

    @Column(name = "validity_status")
    @Enumerated(EnumType.STRING)
    private ValidityStatusEnum validityStatus = ValidityStatusEnum.VALID;

    @Column(name = "lookup_status")
    @Enumerated(EnumType.STRING)
    private LookupStatusEnum lookupStatus = LookupStatusEnum.NOT_CHECKED;

    @Column(name = "je_status")
    @Enumerated(EnumType.STRING)
    private PostedStatusEnum journalEntryStatus = PostedStatusEnum.NOT_POSTED;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
