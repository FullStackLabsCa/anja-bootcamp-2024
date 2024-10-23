package io.reactivestax.producer.type.exception;

public class InvalidPersistenceTechnologyException extends RuntimeException {
    public InvalidPersistenceTechnologyException(String message) {
        super(message);
    }
}