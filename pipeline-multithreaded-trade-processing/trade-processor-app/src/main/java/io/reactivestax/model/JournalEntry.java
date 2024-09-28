package io.reactivestax.model;

import java.util.Date;

public class JournalEntry {
    private String accountNumber;
    private String securityCusip;
    private String direction;
    private int quantity;
    private String postedStatus;
    private Date transactionTime;

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getSecurityCusip() {
        return securityCusip;
    }

    public void setSecurityCusip(String securityCusip) {
        this.securityCusip = securityCusip;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getPostedStatus() {
        return postedStatus;
    }

    public void setPostedStatus(String postedStatus) {
        this.postedStatus = postedStatus;
    }

    public Date getTransactionTime() {
        return transactionTime;
    }

    public void setTransactionTime(Date transactionTime) {
        this.transactionTime = transactionTime;
    }
}
