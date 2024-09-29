package io.reactivestax.model;


import java.time.LocalDateTime;

public class JournalEntry {
    private final String accountNumber;
    private final String securityCusip;
    private final String direction;
    private final int quantity;
    private final String postedStatus;
    private final LocalDateTime transactionTime;

    public JournalEntry(String accountNumber, String securityCusip, String direction, int quantity, String postedStatus, LocalDateTime transactionTime) {
        this.accountNumber = accountNumber;
        this.securityCusip = securityCusip;
        this.direction = direction;
        this.quantity = quantity;
        this.postedStatus = postedStatus;
        this.transactionTime = transactionTime;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public String getSecurityCusip() {
        return securityCusip;
    }

    public String getDirection() {
        return direction;
    }

    public int getQuantity() {
        return quantity;
    }

    public String getPostedStatus() {
        return postedStatus;
    }

    public LocalDateTime getTransactionTime() {
        return transactionTime;
    }
}
