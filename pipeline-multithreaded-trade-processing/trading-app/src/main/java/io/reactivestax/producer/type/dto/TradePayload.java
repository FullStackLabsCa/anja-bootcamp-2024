package io.reactivestax.producer.type.dto;

import io.reactivestax.producer.type.enums.ValidityStatus;
import lombok.Data;

@Data
public class TradePayload {
    private String tradeNumber;
    private String payload;
    private String validityStatus = ValidityStatus.VALID.toString();
}
