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
    public static final String INVALID_OTP = "Invalid otp. Please enter correct otp.";
    public static final String OTP_EXPIRED_NOT_GENERATED = "Otp expired/not generated. Please generate a new otp.";
    public static final String EMPTY_MESSAGE = "Message is required.";
    public static final String INVALID_CUSTOMER_ID = "Invalid Customer ID.";
    public static final String OTP_ATTEMPTS_EXCEEDED = "You have exceeded the number of allowed OTP attempts. Please " +
            "try again later.";
    public static final String OTP_VERIFICATION_FAILED_WITH_ATTEMPTS_EXCEEDED = "Otp verification failed. You have exceeded " +
            "the number of allowed OTP verification attempts. Please try again later.";
    public static final String OTP_VERIFICATION_ATTEMPTS_EXCEEDED = "You have exceeded the number of allowed OTP " +
            " verification attempts. Please try again later.";
    public static final String NOT_A_VALIDATED_USER = "User not validated. Please generate a new otp and verify.";
}