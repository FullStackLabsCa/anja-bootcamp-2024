package io.reactivestax.model;

public class Position {
    private String accountNumber;
    private String securityCusip;
    private int quantity;

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

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
