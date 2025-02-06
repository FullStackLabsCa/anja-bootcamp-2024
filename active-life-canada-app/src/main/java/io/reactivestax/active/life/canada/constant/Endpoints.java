package io.reactivestax.active.life.canada.constant;

public class Endpoints {
    private Endpoints() {
    }

    public static final String BASE_ENDPOINT = "/api/v1";
    public static final String LOGIN = "/login";
    public static final String LOGIN_2FA = "/login/2fa";
    public static final String SIGNUP = "/signUp";
    public static final String ACTIVATION = "/activate/{activationId}";
    public static final String DASHBOARD = "/dashboard";
    public static final String MEMBERS_BASE = "/members";
    public static final String MEMBER_ID = "/{memberId}";
    public static final String REGISTERED_COURSES = "/registeredCourses";
    public static final String WAITLISTED_COURSES = "/waitlistedCourses";
    public static final String OFFERED_COURSES = "/offeredCourses";
    public static final String ENROLL_COURSE = OFFERED_COURSES + "/{barCode}/enrollments/{memberLoginId}";
    public static final String WITHDRAW_FROM_COURSE = "/enrollments/{enrollmentId}";
    public static final String SEARCH_OFFERED_COURSES = OFFERED_COURSES + "/search";
    public static final String OFFERED_COURSE_CART = OFFERED_COURSES + "/addToCart";
    public static final String CART_PAYMENT = "/cart/payment";

    // stripe end points
    public static final String STRIPE_PAYMENT_INTENTS = "/payment_intents";
    public static final String STRIPE_PAYMENT_METHODS = "/payment_methods";
    public static final String STRIPE_PAYMENT_INTENTS_CONFIRM = "/payment_intents/{paymentIntentId}/confirm";


    // ENS endpoints
    public static final String ENS_SMS = "/ems/sms";
    public static final String ENS_EMAIL = "/ems/email";
    public static final String ENS_SMS_OTP = "/otp/sms";
    public static final String ENS_EMAIL_OTP = "/otp/email";
    public static final String ENS_VERIFY_OTP = "/otp/verify";
    public static final String ACTIVATION_LINK_URL = "http://localhost:8082/api/v1/activate/{0}";
}
