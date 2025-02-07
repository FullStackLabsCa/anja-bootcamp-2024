package io.reactivestax.active.life.canada.constant;

public class ExceptionHandlerConst {

    private ExceptionHandlerConst() {
    }

    public static final String CODE = "code";
    public static final String TYPE = "type";
    public static final String MESSAGE = "message";
    public static final String TIMESTAMP = "timestamp";

    public static final String MEMBER_ALREADY_EXISTS = "Username already exists, please try with a different username.";
    public static final String MEMBER_INACTIVE = "Member is no longer active.";
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
    public static final String COURSE_NO_LONGER_AVAILABLE = "Course with barCode: {0} is no longer available.";
    public static final String ALREADY_ENROLLED = "Enrollment failed, already enrolled in this course.";
    public static final String ALREADY_WAITLISTED = "Adding to waitlist failed, already part of the waitlist.";
    public static final String WAITLIST_ADD_FAILED_ALREADY_ENROLLED = "Adding to waitlist failed, already enrolled in the course.";
    public static final String WAITLIST_FULL = "Cannot add to waitlist, waitlist is full.";
    public static final String TOKEN_EXPIRED = "Verification failed, token expired, please generate a new one.";
    public static final String ADD_TO_CART_FAILED_WAITLIST = "Course cannot be added to cart, course is in waitlist.";
    public static final String ADD_TO_CART_FAILED_NOT_AVAILABLE = "Course cannot be added to cart, course is not available.";
    public static final String ADD_TO_CART_FAILED_ALREADY_IN_CART = "Course cannot be added to cart, it is already present in your cart.";
    public static final String EMPTY_CART = "Cart is empty.";

    // Validation Constants
    public static final String MIN_PASSWORD = "Password must be minimum of 5 characters.";
    public static final String INVALID_EMAIL = "Invalid email Id.";
    public static final String INVALID_HOME_PHONE = "Invalid home phone.";
    public static final String INVALID_BUSINESS_PHONE = "Invalid business phone.";
    public static final String EMPTY_COURSE_ID = "Course Id cannot be empty.";
    public static final String EMPTY_FACILITY_ID = "Facility Id cannot be empty.";
    public static final String EMPTY_NAME = "Name cannot be empty.";
    public static final String EMPTY_USERNAME = "Username cannot be empty.";
    public static final String EMPTY_PASSWORD = "Password cannot be empty.";
    public static final String EMPTY_BAR_CODE = "Password cannot be empty.";
    public static final String EMPTY_RESIDENT_FEE = "Resident fee cannot be empty.";
    public static final String EMPTY_NON_RESIDENT_FEE = "Non resident fee cannot be empty.";
    public static final String EMPTY_STREET_NO = "Street no cannot be empty.";
    public static final String EMPTY_STREET_NAME = "Street name cannot be empty.";
    public static final String EMPTY_CITY = "City cannot be empty.";
    public static final String EMPTY_PROVINCE = "Province cannot be empty.";
    public static final String EMPTY_COUNTRY = "Country cannot be empty.";
    public static final String EMPTY_TOKEN = "Token cannot be empty.";
    public static final String EMPTY_OTP = "Otp cannot be empty.";
    public static final String NULL_NO_OF_CLASSES = "No of classes offered cannot be empty.";
    public static final String NULL_IS_ALL_DAY_COURSE = "Is all day course cannot be empty.";
    public static final String NULL_NO_OF_SPOTS = "No of spots cannot be empty.";
}
