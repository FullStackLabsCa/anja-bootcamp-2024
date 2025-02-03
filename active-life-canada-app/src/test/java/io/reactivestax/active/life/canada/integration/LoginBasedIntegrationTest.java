package io.reactivestax.active.life.canada.integration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.reactivestax.active.life.canada.constant.Endpoints;
import io.reactivestax.active.life.canada.constant.Message;
import io.reactivestax.active.life.canada.constant.TestData;
import io.reactivestax.active.life.canada.dto.CreateMemberRequest;
import io.reactivestax.active.life.canada.dto.SuccessfulResponse;
import io.reactivestax.active.life.canada.entity.AccountActivationRequest;
import io.reactivestax.active.life.canada.entity.Course;
import io.reactivestax.active.life.canada.enums.PreferredModeOfCommunication;
import io.reactivestax.active.life.canada.repository.AccountActivationRequestRepository;
import io.reactivestax.active.life.canada.repository.CourseRepository;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.time.LocalDate;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class LoginBasedIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AccountActivationRequestRepository accountActivationRequestRepository;

    @Autowired
    private CourseRepository courseRepository;

    private String baseUrl;

    @BeforeAll
    void setup() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = port;
        baseUrl = "http://localhost:" + port + Endpoints.BASE_ENDPOINT;
    }

    @Test
    void testActiveLifeCanadaApp() throws JsonProcessingException {
        testSignUp();
        testActivate();
    }

    private void testSignUp() throws JsonProcessingException {
        CreateMemberRequest createMemberRequest = CreateMemberRequest.builder()
                .name(TestData.MEMBER_NAME)
                .username(TestData.MEMBER_LOGIN_ID)
                .password(TestData.PASSWORD)
                .dob(LocalDate.now())
                .emailId(TestData.EMAIL)
                .city(TestData.CITY1)
                .province(TestData.PROVINCE)
                .country(TestData.COUNTRY)
                .homePhone(TestData.HOME_PHONE)
                .preferredModeOfCommunication(PreferredModeOfCommunication.HOME_PHONE)
                .build();

        Response response = given()
                .log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(objectMapper.writeValueAsString(createMemberRequest))
                .when()
                .post(baseUrl + Endpoints.SIGNUP)
                .then()
                .log().all()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .response();

        SuccessfulResponse successfulResponse = response.as(SuccessfulResponse.class);
        assertThat(successfulResponse).isNotNull();
        assertThat(successfulResponse.getMessage()).isEqualTo(Message.SIGNUP_SUCCESSFUL);
    }

    private void testActivate() {
        AccountActivationRequest accountActivationRequest = accountActivationRequestRepository.findAll().get(0);

        Response response = given()
                .log().all()
                .when()
                .get(baseUrl + "/activate/" + accountActivationRequest.getToken().toString())
                .then()
                .log().all()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .response();

        SuccessfulResponse successfulResponse = response.as(SuccessfulResponse.class);
        assertThat(successfulResponse).isNotNull();
        assertThat(successfulResponse.getMessage()).isEqualTo(Message.ACTIVATED_SUCCESSFULLY);
    }
}
