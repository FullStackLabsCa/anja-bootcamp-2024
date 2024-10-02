package io.reactivestax.model;


import java.time.LocalDateTime;

public record JournalEntry(String tradeId, String accountNumber, String securityCusip, String direction, int quantity,
                           String postedStatus, LocalDateTime transactionTime) {
}
