package io.reactivestax.type.exception;

public class QueryFailedException extends RuntimeException {
    public QueryFailedException(String message) {
        super(message);
    }
}
