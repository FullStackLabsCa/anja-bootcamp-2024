package io.reactivestax.repository.hibernate.entity;

import io.reactivestax.type.enums.LookupStatus;
import io.reactivestax.type.enums.PostedStatus;
import io.reactivestax.type.enums.ValidityStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.sql.Timestamp;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "trade_payloads")
public class TradePayload {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "trade_number", nullable = false, unique = true)
    private String tradeNumber;

    @Column(name = "payload", nullable = false)
    private String payload;

    @Column(name = "validity_status", nullable = false)
    @Enumerated(EnumType.STRING)
    private ValidityStatus validityStatus = ValidityStatus.VALID;

    @Column(name = "lookup_status", nullable = false)
    @Enumerated(EnumType.STRING)
    private LookupStatus lookupStatus = LookupStatus.NOT_CHECKED;

    @Column(name = "je_status", nullable = false)
    @Enumerated(EnumType.STRING)
    private PostedStatus journalEntryStatus = PostedStatus.NOT_POSTED;

    @CreationTimestamp
    @Column(name = "created_timestamp", updatable = false, nullable = false)
    private Timestamp createdTimestamp;

    @UpdateTimestamp
    @Column(name = "updated_timestamp", nullable = false)
    private Timestamp updatedTimestamp;
}
