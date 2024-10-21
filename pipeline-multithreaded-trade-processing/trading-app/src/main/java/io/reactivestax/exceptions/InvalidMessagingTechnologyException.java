package io.reactivestax.exceptions;

public class InvalidMessagingTechnologyException extends RuntimeException {
    public InvalidMessagingTechnologyException(String message) {
        super(message);
    }

    public InvalidMessagingTechnologyException(String message, Throwable cause) {
        super(message, cause);
    }
}
