package io.reactivestax.active.life.canada.integration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.reactivestax.active.life.canada.constant.Endpoints;
import io.reactivestax.active.life.canada.constant.Message;
import io.reactivestax.active.life.canada.constant.TestData;
import io.reactivestax.active.life.canada.dto.*;
import io.reactivestax.active.life.canada.entity.*;
import io.reactivestax.active.life.canada.enums.PreferredModeOfCommunication;
import io.reactivestax.active.life.canada.model.SecurityHeader;
import io.reactivestax.active.life.canada.repository.AccountActivationRequestRepository;
import io.reactivestax.active.life.canada.repository.FamilyCourseRegistrationRepository;
import io.reactivestax.active.life.canada.repository.FamilyMemberRepository;
import io.reactivestax.active.life.canada.repository.OfferedCourseRepository;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ActiveLifeCanadaAppIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private ObjectMapper objectMapper;

    private String baseUrl;

    @Autowired
    private OfferedCourseRepository offeredCourseRepository;

    @Autowired
    private AccountActivationRequestRepository accountActivationRequestRepository;

    @Autowired
    private FamilyMemberRepository familyMemberRepository;

    @Autowired
    private FamilyCourseRegistrationRepository familyCourseRegistrationRepository;

    @MockitoBean
    private RestTemplate restTemplate;

    private final String securityHeader = "X-security-header";

    private final SecurityHeader securityHeaderObject = new SecurityHeader();

    @BeforeAll
    void setup() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = port;
        baseUrl = "http://localhost:" + port + Endpoints.BASE_ENDPOINT;
    }

    @Test
    void testActiveLifeCanadaApp() throws JsonProcessingException {
        testAuthenticationManagement();
        testProgramManagement();
        testCourseRegistrationManagement();
        testFamilyManagement();
    }

    private void testAuthenticationManagement() throws JsonProcessingException {
        testSignUp();
        testActivate();
    }

    private void testProgramManagement() throws JsonProcessingException {
        testCreateOfferCourse();
        testUpdateOfferedCourse();
        testGetOfferedCourses();
        testSearchOfferedCourses();
    }

    private void testCourseRegistrationManagement() throws JsonProcessingException {
        testEnrollIntoCourse();
        testGetRegisteredCourses();
        testGetWaitlistedCourses();
        testGetDashboard();
        testWithdrawFromCourse();
    }

    private void testFamilyManagement() throws JsonProcessingException {
        testCreateMember();
        testUpdateMember();
        testGetMember();
        testDeactivateMember();
    }

    private void testSignUp() throws JsonProcessingException {
        CreateMemberRequest createMemberRequest = CreateMemberRequest.builder()
                .name(TestData.MEMBER_NAME)
                .username(TestData.MEMBER_LOGIN_ID)
                .password(TestData.PASSWORD)
                .dob(LocalDate.now())
                .emailId(TestData.EMAIL)
                .streetNo("123")
                .streetName("Lester")
                .city(TestData.CITY1)
                .province(TestData.PROVINCE)
                .country(TestData.COUNTRY)
                .homePhone(TestData.HOME_PHONE)
                .preferredModeOfCommunication(PreferredModeOfCommunication.HOME_PHONE)
                .build();

        when(restTemplate.exchange(anyString(), any(HttpMethod.class), any(HttpEntity.class), eq(String.class))).thenReturn(new ResponseEntity<>(HttpStatus.OK));

        Response response = given().log().all().contentType(MediaType.APPLICATION_JSON_VALUE).body(objectMapper.writeValueAsString(createMemberRequest)).when().post(baseUrl + Endpoints.SIGNUP).then().log().all().statusCode(HttpStatus.OK.value()).extract().response();

        SuccessfulResponse successfulResponse = response.as(SuccessfulResponse.class);
        assertThat(successfulResponse).isNotNull();
        assertThat(successfulResponse.getMessage()).isEqualTo(Message.SIGNUP_SUCCESSFUL);
    }

    private void testActivate() {
        AccountActivationRequest accountActivationRequest = accountActivationRequestRepository.findAll().get(0);

        Response response = given().log().all().pathParam("activationId", accountActivationRequest.getToken().toString()).when().get(baseUrl + Endpoints.ACTIVATION).then().log().all().statusCode(HttpStatus.OK.value()).extract().response();

        SuccessfulResponse successfulResponse = response.as(SuccessfulResponse.class);
        assertThat(successfulResponse).isNotNull();
        assertThat(successfulResponse.getMessage()).isEqualTo(Message.ACTIVATED_SUCCESSFULLY);

        FamilyMember familyMember = familyMemberRepository.findAll().get(0);
        securityHeaderObject.setFamilyMemberId(familyMember.getFamilyMemberId().toString());
    }

    private void testCreateOfferCourse() throws JsonProcessingException {
        OfferCourseRequest offerCourseRequest = OfferCourseRequest.builder().courseId(1L).facilityId(1L).noOfSpots(10).startDate(LocalDate.now().plusDays(2)).endDate(LocalDate.now().plusDays(10)).startTime(LocalTime.now()).endTime(LocalTime.now().plusHours(2)).isAllDayCourse(true).registrationStartDate(LocalDate.now()).residentCourseFee(180).nonResidentCourseFee(200).noOfClassesOffered(10).build();

        Response response = given().log().all().contentType(MediaType.APPLICATION_JSON_VALUE).body(objectMapper.writeValueAsString(offerCourseRequest)).when().post(baseUrl + Endpoints.OFFERED_COURSES).then().log().all().statusCode(HttpStatus.OK.value()).extract().response();

        SuccessfulResponse successfulResponse = response.as(SuccessfulResponse.class);
        assertThat(successfulResponse).isNotNull();
        assertThat(successfulResponse.getMessage()).isEqualTo(Message.OFFERED_COURSE_ADDED);
    }

    private void testUpdateOfferedCourse() throws JsonProcessingException {
        OfferedCourse offeredCourse = offeredCourseRepository.findAll().get(0);

        CourseUpdateRequest courseUpdateRequest = CourseUpdateRequest.builder().barCode(offeredCourse.getBarCode().toString()).isAllDayCourse(false).build();

        Response response = given().log().all().contentType(MediaType.APPLICATION_JSON_VALUE).body(objectMapper.writeValueAsString(courseUpdateRequest)).when().put(baseUrl + Endpoints.OFFERED_COURSES).then().log().all().statusCode(HttpStatus.OK.value()).extract().response();

        SuccessfulResponse successfulResponse = response.as(SuccessfulResponse.class);
        assertThat(successfulResponse).isNotNull();
        assertThat(successfulResponse.getMessage()).isEqualTo(Message.OFFERED_COURSE_UPDATED);
    }

    private void testGetOfferedCourses() {
        Response response = given().log().all().when().get(baseUrl + Endpoints.OFFERED_COURSES).then().log().all().statusCode(HttpStatus.OK.value()).extract().response();

        List<OfferedCourse> offeredCourses = response.jsonPath().getList(".", OfferedCourse.class);
        assertThat(offeredCourses).isNotNull();
        assertEquals(1, offeredCourses.size());
    }

    private void testSearchOfferedCourses() throws JsonProcessingException {
        OfferedCourse offeredCourse = offeredCourseRepository.findAll().get(0);

        OfferedCourseSearchRequest offeredCourseSearchRequest = new OfferedCourseSearchRequest(offeredCourse.getCourse().getName(), offeredCourse.getStartDate(), offeredCourse.getEndDate(), offeredCourse.getFacility().getCity(), offeredCourse.getFacility().getProvince(), offeredCourse.getCourse().getSubCategory().getCategory().getName(), offeredCourse.getCourse().getSubCategory().getName(), offeredCourse.getCourse().getAgeGroup().getShortCode());

        Response response = given().contentType(MediaType.APPLICATION_JSON_VALUE).body(objectMapper.writeValueAsString(offeredCourseSearchRequest)).log().all().when().post(baseUrl + Endpoints.SEARCH_OFFERED_COURSES).then().log().all().statusCode(HttpStatus.OK.value()).extract().response();

        List<OfferedCourse> offeredCourses = response.jsonPath().getList(".", OfferedCourse.class);
        assertThat(offeredCourses).isNotNull();
        assertEquals(1, offeredCourses.size());
    }

    private void testEnrollIntoCourse() throws JsonProcessingException {
        OfferedCourse offeredCourse = offeredCourseRepository.findAll().get(0);
        Response response = given().log().all().header(securityHeader, objectMapper.writeValueAsString(securityHeaderObject)).pathParams("barCode", offeredCourse.getBarCode().toString()).pathParam("memberLoginId", TestData.MEMBER_LOGIN_ID).when().post(baseUrl + Endpoints.ENROLL_COURSE).then().log().all().statusCode(HttpStatus.OK.value()).extract().response();

        SuccessfulResponse successfulResponse = response.as(SuccessfulResponse.class);
        assertThat(successfulResponse).isNotNull();
        assertThat(successfulResponse.getMessage()).isEqualTo(Message.ENROLLMENT_SUCCESSFUL);
    }

    private void testGetRegisteredCourses() {
        Response response = given().header(securityHeader, securityHeaderObject).log().all().when().get(Endpoints.BASE_ENDPOINT + Endpoints.REGISTERED_COURSES).then().log().all().statusCode(HttpStatus.OK.value()).extract().response();

        List<FamilyCourseRegistration> familyCourseRegistrationList = response.jsonPath().getList(".", FamilyCourseRegistration.class);
        assertThat(familyCourseRegistrationList).isNotNull();
        assertEquals(1, familyCourseRegistrationList.size());
    }

    private void testGetWaitlistedCourses() {
        Response response = given().header(securityHeader, securityHeaderObject).log().all().when().get(Endpoints.BASE_ENDPOINT + Endpoints.WAITLISTED_COURSES).then().log().all().statusCode(HttpStatus.OK.value()).extract().response();

        List<OfferedCourseWaitlist> offeredCourseWaitlist = response.jsonPath().getList(".", OfferedCourseWaitlist.class);
        assertThat(offeredCourseWaitlist).isNotNull();
        assertEquals(0, offeredCourseWaitlist.size());
    }

    private void testGetDashboard() {
        Response response = given().header(securityHeader, securityHeaderObject).log().all().when().get(Endpoints.BASE_ENDPOINT + Endpoints.DASHBOARD).then().log().all().statusCode(HttpStatus.OK.value()).extract().response();

        DashboardDto dashboardDto = response.as(DashboardDto.class);
        assertThat(dashboardDto).isNotNull();
        assertEquals(1, dashboardDto.getRegisteredCourses().size());
        assertEquals(0, dashboardDto.getWaitlistedCourses().size());
    }

    private void testWithdrawFromCourse() {
        FamilyCourseRegistration familyCourseRegistration = familyCourseRegistrationRepository.findAll().get(0);
        Response response = given().log().all().pathParam("enrollmentId", familyCourseRegistration.getFamilyCourseRegistrationId().toString()).header(securityHeader, securityHeaderObject).when().delete(Endpoints.BASE_ENDPOINT + Endpoints.WITHDRAW_FROM_COURSE).then().log().all().statusCode(HttpStatus.OK.value()).extract().response();

        SuccessfulResponse successfulResponse = response.as(SuccessfulResponse.class);
        assertThat(successfulResponse).isNotNull();
        assertEquals(Message.WITHDRAWN_SUCCESSFUL, successfulResponse.getMessage());
    }

    private void testCreateMember() throws JsonProcessingException {
        CreateMemberRequest createMemberRequest = CreateMemberRequest.builder()
                .name(TestData.MEMBER_NAME)
                .username(TestData.FAMILY_MEMBER_LOGIN_ID)
                .password(TestData.PASSWORD).dob(LocalDate.now())
                .emailId(TestData.EMAIL).city(TestData.CITY1)
                .streetNo("123")
                .streetName("Lester")
                .province(TestData.PROVINCE)
                .country(TestData.COUNTRY)
                .homePhone(TestData.HOME_PHONE)
                .preferredModeOfCommunication(PreferredModeOfCommunication.HOME_PHONE)
                .build();

        when(restTemplate.exchange(anyString(), any(HttpMethod.class), any(HttpEntity.class), eq(String.class))).thenReturn(new ResponseEntity<>(HttpStatus.OK));

        Response response = given().log().all().contentType(MediaType.APPLICATION_JSON_VALUE).body(objectMapper.writeValueAsString(createMemberRequest)).header(securityHeader, securityHeaderObject).when().post(baseUrl + Endpoints.MEMBERS_BASE).then().log().all().statusCode(HttpStatus.OK.value()).extract().response();

        SuccessfulResponse successfulResponse = response.as(SuccessfulResponse.class);
        assertThat(successfulResponse).isNotNull();
        assertThat(successfulResponse.getMessage()).isEqualTo(Message.MEMBER_ADD_SUCCESSFUL);
    }

    private void testUpdateMember() throws JsonProcessingException {
        UpdateMemberRequest updateMemberRequest = new UpdateMemberRequest();
        updateMemberRequest.setName("Updated name");

        Response response = given().log().all().contentType(MediaType.APPLICATION_JSON_VALUE).body(objectMapper.writeValueAsString(updateMemberRequest)).header(securityHeader, securityHeaderObject).pathParam("memberId", TestData.MEMBER_LOGIN_ID).when().patch(baseUrl + Endpoints.MEMBERS_BASE + Endpoints.MEMBER_ID).then().log().all().statusCode(HttpStatus.OK.value()).extract().response();

        SuccessfulResponse successfulResponse = response.as(SuccessfulResponse.class);
        assertThat(successfulResponse).isNotNull();
        assertThat(successfulResponse.getMessage()).isEqualTo(Message.MEMBER_UPDATED);
    }

    private void testGetMember() {
        Response response = given().log().all().contentType(MediaType.APPLICATION_JSON_VALUE).header(securityHeader, securityHeaderObject).pathParam("memberId", TestData.MEMBER_LOGIN_ID).when().get(baseUrl + Endpoints.MEMBERS_BASE + Endpoints.MEMBER_ID).then().log().all().statusCode(HttpStatus.OK.value()).extract().response();

        MemberDetails memberDetails = response.as(MemberDetails.class);
        assertThat(memberDetails).isNotNull();
        assertEquals("Updated name", memberDetails.getName());
    }

    private void testDeactivateMember() {
        Response response = given().log().all().contentType(MediaType.APPLICATION_JSON_VALUE).header(securityHeader, securityHeaderObject).pathParam("memberId", TestData.MEMBER_LOGIN_ID).when().delete(baseUrl + Endpoints.MEMBERS_BASE + Endpoints.MEMBER_ID).then().log().all().statusCode(HttpStatus.OK.value()).extract().response();

        SuccessfulResponse successfulResponse = response.as(SuccessfulResponse.class);
        assertThat(successfulResponse).isNotNull();
        assertEquals(Message.MEMBER_DEACTIVATED, successfulResponse.getMessage());
    }
}
