package io.reactivestax.type.exception;

import io.reactivestax.util.Constants;

public class InvalidPersistenceTechnologyException extends RuntimeException {
    public InvalidPersistenceTechnologyException() {
        super(Constants.INVALID_PERSISTENCE_TECHNOLOGY);
    }
}