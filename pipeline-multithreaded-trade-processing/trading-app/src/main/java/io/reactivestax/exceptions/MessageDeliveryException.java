package io.reactivestax.exceptions;

public class MessageDeliveryException extends RuntimeException {
    public MessageDeliveryException(String message) {
        super(message);
    }
}
