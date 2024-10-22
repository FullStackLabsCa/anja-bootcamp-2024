package io.reactivestax.consumer.type.exception;

public class InvalidMessagingTechnologyException extends RuntimeException {
    public InvalidMessagingTechnologyException(String message) {
        super(message);
    }

    public InvalidMessagingTechnologyException(String message, Throwable cause) {
        super(message, cause);
    }
}
