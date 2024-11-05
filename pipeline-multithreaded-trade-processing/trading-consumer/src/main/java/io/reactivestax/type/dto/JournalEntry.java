package io.reactivestax.type.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class JournalEntry {
    private Long id;
    private String tradeId;
    private String accountNumber;
    private String securityCusip;
    private String direction;
    private int quantity;
    private String postedStatus;
    private String transactionTimestamp;
}
