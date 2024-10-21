package io.reactivestax.exceptions;

public class HikariCPConnectionException extends RuntimeException {
    public HikariCPConnectionException(String message, Throwable cause) {
        super(message, cause);
    }
}