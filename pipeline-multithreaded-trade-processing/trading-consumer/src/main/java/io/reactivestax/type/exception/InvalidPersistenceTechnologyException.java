package io.reactivestax.type.exception;

import static io.reactivestax.type.Constants.INVALID_PERSISTENCE_TECHNOLOGY;

public class InvalidPersistenceTechnologyException extends RuntimeException {
    public InvalidPersistenceTechnologyException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidPersistenceTechnologyException(String message) {
        super(message);
    }

    public InvalidPersistenceTechnologyException() {
        super(INVALID_PERSISTENCE_TECHNOLOGY);
    }

}