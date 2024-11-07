package io.reactivestax.type.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TradePayloadDTO {
    private Long id;
    private String tradeNumber;
    private String payload;
    private String validityStatus;
    private String lookupStatus;
    private String journalEntryStatus;
}
