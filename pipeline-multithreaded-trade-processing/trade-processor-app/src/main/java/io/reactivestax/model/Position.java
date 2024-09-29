package io.reactivestax.model;

public class Position {
    private final String accountNumber;
    private final String securityCusip;
    private int quantity;

    public Position(String accountNumber, String securityCusip, int quantity) {
        this.accountNumber = accountNumber;
        this.securityCusip = securityCusip;
        this.quantity = quantity;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public String getSecurityCusip() {
        return securityCusip;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
