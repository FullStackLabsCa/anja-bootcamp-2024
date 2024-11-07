package io.reactivestax.type.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
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
