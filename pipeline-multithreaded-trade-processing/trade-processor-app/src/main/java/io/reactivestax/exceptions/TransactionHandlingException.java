package io.reactivestax.exceptions;

public class TransactionHandlingException extends RuntimeException {
    public TransactionHandlingException(String message, Throwable cause) {
        super(message, cause);
    }
}