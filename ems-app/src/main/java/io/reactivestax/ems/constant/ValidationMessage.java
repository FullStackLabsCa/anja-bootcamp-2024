package io.reactivestax.ems.constant;

public class ValidationMessage {

    private ValidationMessage() {
    }

    public static final String EMPTY_CUSTOMER_ID = "Customer id is required.";
    public static final String EMPTY_PHONE_NUMBER = "Phone number is required.";
    public static final String INVALID_PHONE_NUMBER = "Invalid phone number.";
    public static final String INVALID_PHONE_NUMBER_REGEX = "Invalid phone number, it should be in the format 999-999-9999.";
    public static final String EMPTY_EMAIL = "Email is required.";
    public static final String INVALID_EMAIL = "Invalid email.";
    public static final String EMPTY_MESSAGE = "Message is required.";
    public static final String INVALID_CUSTOMER_ID = "Invalid Customer ID.";
    public static final String OTP_ATTEMPTS_EXCEEDED = "You have exceeded the number of allowed OTP attempts. Please " +
            "try again later.";
}
