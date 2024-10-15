package io.reactivestax.model;

public class Position {
    private final String accountNumber;
    private final String securityCusip;
    private int positions;
    private int version;

    public Position(String accountNumber, String securityCusip, int positions, int version) {
        this.accountNumber = accountNumber;
        this.securityCusip = securityCusip;
        this.positions = positions;
        this.version = version;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public String getSecurityCusip() {
        return securityCusip;
    }

    public int getPositions() {
        return positions;
    }

    public void setPositions(int positions) {
        this.positions = positions;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }
}