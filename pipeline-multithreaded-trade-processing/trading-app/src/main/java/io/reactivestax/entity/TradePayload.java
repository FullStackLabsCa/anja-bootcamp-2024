package io.reactivestax.entity;

import io.reactivestax.enums.LookupStatus;
import io.reactivestax.enums.PostedStatus;
import io.reactivestax.enums.ValidityStatus;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Table(name = "trade_payloads")
public class TradePayload extends CustomTimestamp {

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
}
