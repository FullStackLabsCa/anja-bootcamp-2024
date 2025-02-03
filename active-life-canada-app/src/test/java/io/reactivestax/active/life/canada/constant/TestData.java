package io.reactivestax.active.life.canada.constant;

import java.util.UUID;

public class TestData {
    private TestData() {
    }

    public static final UUID UUID_ID = UUID.randomUUID();
    public static final String STRING_ID = UUID_ID.toString();
    public static final UUID FAMILY_MEMBER_ID_UUID = UUID.randomUUID();
    public static final String FAMILY_MEMBER_ID_STRING = FAMILY_MEMBER_ID_UUID.toString();
    public static final UUID FAMILY_GROUP_ID_UUID = UUID.randomUUID();
    public static final String FAMILY_GROUP_ID_STRING = FAMILY_GROUP_ID_UUID.toString();
    public static final UUID LOGGED_IN_MEMBER_ID_UUID = UUID.randomUUID();
    public static final String LOGGED_IN_MEMBER_ID_STRING = FAMILY_MEMBER_ID_UUID.toString();
    public static final UUID OFFERED_COURSE_ID_UUID = UUID.randomUUID();
    public static final String OFFERED_COURSE_ID_STRING = OFFERED_COURSE_ID_UUID.toString();
    public static final UUID UUID_TOKEN = UUID.randomUUID();
    public static final String UUID_TOKEN_STRING = UUID_TOKEN.toString();
    public static final UUID BAR_CODE_UUID = UUID.randomUUID();
    public static final String BAR_CODE_STRING = BAR_CODE_UUID.toString();
    public static final String MEMBER_LOGIN_ID = "member_login_id";
    public static final String FAMILY_MEMBER_LOGIN_ID = "family_member_login_id";
    public static final String INVALID_TOKEN = "invalid_token";
    public static final String LOGGED_IN_MEMBER_NAME = "John Singh";
    public static final String MEMBER_NAME = "John Doe";
    public static final String COURSE_NAME = "Basketball";
    public static final String USERNAME = "user123";
    public static final String WRONG_USERNAME = "wrong_username";
    public static final String PASSWORD = "1234";
    public static final String WRONG_PASSWORD = "wrong_password";
    public static final String OTP = "123456";
    public static final String WRONG_OTP = "wrong_otp";
    public static final String CITY1 = "Mississauga";
    public static final String CITY2 = "Toronto";

    public static final String SECURITY_HEADER_JSON = "{\"familyMemberId\": \"" + LOGGED_IN_MEMBER_ID_STRING + "\"}";
}
