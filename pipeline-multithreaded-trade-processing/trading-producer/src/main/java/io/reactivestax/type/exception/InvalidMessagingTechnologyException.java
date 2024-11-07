package io.reactivestax.type.exception;

import io.reactivestax.util.Constants;

public class InvalidMessagingTechnologyException extends RuntimeException {
    public InvalidMessagingTechnologyException() {
        super(Constants.INVALID_MESSAGING_TECHNOLOGY);
    }
}
