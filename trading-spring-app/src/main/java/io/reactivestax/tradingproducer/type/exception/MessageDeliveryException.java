package io.reactivestax.tradingproducer.type.exception;

public class MessageDeliveryException extends RuntimeException {
    public MessageDeliveryException(String message) {
        super(message);
    }
}
