package io.reactivestax.type.dto;

import lombok.Data;

@Data
public class TradePayload {
    private String tradeNumber;
    private String payload;
    private String validityStatus;
    private String lookupStatus;
    private String journalEntryStatus;
}
