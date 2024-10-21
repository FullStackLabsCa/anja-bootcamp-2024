package io.reactivestax.exceptions;

public class OptimisticLockingException extends RuntimeException {
    public OptimisticLockingException(String message, Throwable cause) {
        super(message, cause);
    }

    public OptimisticLockingException(String message) {
        super(message);
    }
}