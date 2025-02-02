package io.reactivestax.active.life.canada.constant;

public class Message {
    private Message() {
    }

    public static final String SIGNUP_SUCCESSFUL = "Member registered successfully and an activation link is sent to you.";
    public static final String MEMBER_ADD_SUCCESSFUL = "Member registered successfully and an activation link is sent" +
            " to the member's preferred mode of communication.";
    public static final String ACTIVATED_SUCCESSFULLY = "Your account activated successfully.";
    public static final String ENROLLMENT_SUCCESSFUL = "Enrollment successful.";
    public static final String ADDED_TO_WAITLIST = "Spots full, added to waitlist.";
    public static final String WITHDRAWN_SUCCESSFUL = "Withdrawn successful.";
    public static final String SUCCESSFUL_LOGIN = "Credentials verified successfully, an otp is sent to your preferred mode of communication for verification.";
    public static final String SUCCESSFUL_LOGIN_VERIFICATION = "Login verification successful.";
    public static final String MEMBER_DEACTIVATED = "Member deactivated successfully.";
    public static final String MEMBER_UPDATED = "Member updated successfully.";
    public static final String OFFERED_COURSE_ADDED = "Offered course added successfully";
    public static final String OFFERED_COURSE_UPDATED = "Offered course updated successfully";

    public static final String LOGIN_INACTIVE_MEMBER = "You are not an active member. An account activation link is " +
            "sent to you.";
    public static final String ACTIVATION_LINK_MESSAGE = "Hello {0}, Your account activation link is: {1}.";
    public static final String SPOT_AVAILABLE_FOR_ENROLLMENT = "Hello {0}, We have an open spot for the {1} course. " +
            "Please enroll fast to avoid staying in waitlist.";
}
