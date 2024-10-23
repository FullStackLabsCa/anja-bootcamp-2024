package io.reactivestax.producer.type.dto;

import io.reactivestax.producer.type.enums.LookupStatus;
import io.reactivestax.producer.type.enums.PostedStatus;
import io.reactivestax.producer.type.enums.ValidityStatus;
import lombok.Data;

@Data
public class TradePayload {
    private String tradeNumber;
    private String payload;
    private String validityStatus = ValidityStatus.VALID.toString();
    private String lookupStatus = LookupStatus.NOT_CHECKED.toString();
    private String journalEntryStatus = PostedStatus.NOT_POSTED.toString();
}
