package io.reactivestax.tradingproducer.type.entity;

import io.reactivestax.tradingproducer.type.enums.ValidityStatus;
import lombok.Data;

@Data
public class TradePayload {
    private String tradeNumber;
    private String payload;
    private String validityStatus = ValidityStatus.VALID.toString();
}
