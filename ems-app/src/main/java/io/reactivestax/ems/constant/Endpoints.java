package io.reactivestax.ems.constant;

public class Endpoints {

    private Endpoints() {
    }

    public static final String ENS_BASE = "/api/v1/ems";
    public static final String OTP_BASE = "/api/v1/otp";
    public static final String SMS = "/sms";
    public static final String CALL = "/call";
    public static final String EMAIL = "/email";
    public static final String VERIFY = "/verify";
    public static final String STATUS_BY_CUSTOMER_ID = "/status/{customerId}";
}
