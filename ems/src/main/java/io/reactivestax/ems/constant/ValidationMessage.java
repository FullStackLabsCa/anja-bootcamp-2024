package io.reactivestax.ems.constant;

public class ValidationMessage {

    private ValidationMessage() {
    }

    public static final String EMPTY_CUSTOMER_ID = "Customer id is required.";
    public static final String EMPTY_PHONE_NUMBER = "Phone number is required.";
    public static final String INVALID_PHONE_NUMBER = "Invalid phone number.";
    public static final String EMPTY_EMAIL = "Email is required.";
    public static final String INVALID_EMAIL = "Invalid email.";
    public static final String EMPTY_MESSAGE = "Message is required.";
}
