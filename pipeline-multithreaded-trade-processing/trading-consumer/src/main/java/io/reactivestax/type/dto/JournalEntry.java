package io.reactivestax.type.dto;

import lombok.Data;

@Data
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
