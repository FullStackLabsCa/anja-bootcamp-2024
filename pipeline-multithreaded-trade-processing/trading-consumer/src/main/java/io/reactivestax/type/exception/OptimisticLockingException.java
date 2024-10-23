package io.reactivestax.consumer.type.exception;

public class OptimisticLockingException extends RuntimeException {
    public OptimisticLockingException(String message, Throwable cause) {
        super(message, cause);
    }

    public OptimisticLockingException(String message) {
        super(message);
    }
}