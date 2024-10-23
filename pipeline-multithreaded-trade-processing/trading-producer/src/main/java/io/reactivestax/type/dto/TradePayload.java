package io.reactivestax.type.dto;

import io.reactivestax.type.enums.ValidityStatus;
import lombok.Data;

@Data
public class TradePayload {
    private String tradeNumber;
    private String payload;
    private String validityStatus = ValidityStatus.VALID.toString();
}
