package io.reactivestax.exception;

public class MaxRetryCountReachedException extends RuntimeException {
    public MaxRetryCountReachedException(String message) {
        super(message);
    }
}
