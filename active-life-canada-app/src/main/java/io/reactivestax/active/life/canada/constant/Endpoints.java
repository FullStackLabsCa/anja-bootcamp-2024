package io.reactivestax.active.life.canada.constant;

public class Endpoints {
    private Endpoints() {
    }

    public static final String BASE_URL = "/api/v1";
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
    public static final String ENROLL_COURSE = "/{offeredCourseId}/enrollments/{familyMemberId}";
    public static final String WITHDRAW_FROM_COURSE = "/enrollments/{enrollmentId}";
    public static final String SEARCH = "/search";
    public static final String OFFER_COURSES_BY_FACILITY = "/facilities/{facilityId}/courses/{courseId}/offeredCourses";

    public static final String ENS_SMS = "http://localhost:8080/api/v1/ems/sms";
    public static final String ACTIVATION_LINK_URL = "http://localhost:8082/api/v1/activate/{0}";
}
