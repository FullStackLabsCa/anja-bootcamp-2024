package io.reactivestax.active.life.canada.constant;

public class ExceptionMessage {

    private ExceptionMessage() {
    }

    public static final String MEMBER_ALREADY_EXISTS = "Username already exists, please try with a different username.";
    public static final String INVALID_ACTIVATION_LINK = "Invalid account activation link.";
    public static final String UNAUTHORIZED_ACCESS = "Unauthorized access.";
    public static final String VERIFICATION_FAILED = "Otp verification failed.";
    public static final String INCORRECT_USERNAME_PASSWORD = "Incorrect username/password.";
    public static final String INCORRECT_TOKEN_OTP = "Incorrect token/otp.";
    public static final String INTERNAL_ERROR = "Internal Server Error.";
    public static final String OTP_SEND_REQUEST_FAILED = "Something went wrong while sending otp.";
    public static final String EMS_SEND_REQUEST_FAILED = "Something went wrong while sending message.";
    public static final String INVALID_MEMBER_ID = "Invalid username.";
    public static final String INVALID_COURSE_ID = "Invalid course Id.";
    public static final String INVALID_OFFERED_COURSE_ID = "Invalid offered course Id.";
    public static final String INVALID_FAMILY_COURSE_REGISTRATION_ID = "Invalid family course registration Id.";
    public static final String INVALID_FACILITY_ID = "Invalid facility Id.";
    public static final String RESIDENT_COURSE_FEE_NOT_FOUND = "Course not available for residents.";
    public static final String NON_RESIDENT_COURSE_FEE_NOT_FOUND = "Course not available for non-residents.";
    public static final String WITHDRAW_NOT_ALLOWED = "Withdraw from the course is not allowed.";
    public static final String COURSE_FULL = "Course spots and waitlist full.";
}
