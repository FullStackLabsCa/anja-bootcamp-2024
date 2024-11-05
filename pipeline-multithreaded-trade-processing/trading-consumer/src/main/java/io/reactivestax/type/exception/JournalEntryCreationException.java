package io.reactivestax.type.exception;

public class JournalEntryCreationException extends RuntimeException {
    public JournalEntryCreationException(String message, Throwable cause) {
        super(message, cause);
    }
}