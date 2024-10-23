package io.reactivestax.producer.util.database.jdbc.entity;

import io.reactivestax.producer.type.enums.LookupStatus;
import io.reactivestax.producer.type.enums.PostedStatus;
import io.reactivestax.producer.type.enums.ValidityStatus;
import lombok.Data;

import java.sql.Timestamp;

@Data
public class TradePayload {
    private Long id;
    private String tradeNumber;
    private String payload;
    private ValidityStatus validityStatus = ValidityStatus.VALID;
    private LookupStatus lookupStatus = LookupStatus.NOT_CHECKED;
    private PostedStatus journalEntryStatus = PostedStatus.NOT_POSTED;
    private Timestamp createdTimestamp;
    private Timestamp updatedTimestamp;
}
