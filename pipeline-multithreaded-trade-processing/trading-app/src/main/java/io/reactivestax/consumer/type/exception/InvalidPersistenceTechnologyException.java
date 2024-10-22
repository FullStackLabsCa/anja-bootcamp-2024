package io.reactivestax.consumer.type.exception;

public class InvalidPersistenceTechnologyException extends RuntimeException {
    public InvalidPersistenceTechnologyException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidPersistenceTechnologyException(String message) {
        super(message);
    }

}