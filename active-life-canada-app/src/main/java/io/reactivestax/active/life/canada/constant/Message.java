package io.reactivestax.active.life.canada.constant;

public class Message {
    private Message() {
    }

    public static final String SIGNUP_SUCCESSFUL = "Member registered successfully and an activation link is sent to you.";
    public static final String MEMBER_ADD_SUCCESSFUL = "Member registered successfully and an activation link is sent" +
            " to the member's preferred mode of communication.";
    public static final String ACTIVATED_SUCCESSFULLY = "Your account activated successfully.";
    public static final String SUCCESSFUL_LOGIN = "Credentials verified successfully, an otp is sent to your preferred mode of communication for verification.";
    public static final String SUCCESSFUL_LOGIN_VERIFICATION = "Login verification successful.";
    public static final String MEMBER_DEACTIVATED = "Member deactivated successfully.";
    public static final String MEMBER_UPDATED = "Member updated successfully.";

    public static final String LOGIN_INACTIVE_MEMBER = "You are not an active member. An account activation link is " +
            "sent to you.";
    public static final String ACTIVATION_LINK_MESSAGE = "Hello {0}, Your account activation link is: {1}.";

}
