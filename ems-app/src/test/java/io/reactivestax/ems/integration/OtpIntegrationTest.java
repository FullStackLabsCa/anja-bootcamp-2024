package io.reactivestax.ems.integration;

import io.reactivestax.ems.constant.Endpoints;
import io.reactivestax.ems.constant.SuccessMessage;
import io.reactivestax.ems.domain.OtpMessage;
import io.reactivestax.ems.dto.BaseDTO;
import io.reactivestax.ems.dto.SuccessfulResponse;
import io.reactivestax.ems.dto.ValidatedOtpDTO;
import io.reactivestax.ems.dto.VerifyOtpDTO;
import io.reactivestax.ems.enums.OtpStatus;
import io.reactivestax.ems.repository.OtpRepository;
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

import java.util.List;
import java.util.Optional;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class OtpIntegrationTest {

    @LocalServerPort
    private int port;

    private String baseUrl;
    private static final String CUSTOMER_ID = "6e6a7d4b-1eaa-4e23-9fd3-8b5d0840d3f1";

    @Autowired
    private OtpRepository otpRepository;

    @BeforeAll
    void setup() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = port;
        baseUrl = "http://localhost:" + port + Endpoints.OTP_BASE;
    }

    @Test
    void testSendOtpViaSms() {
        Response response = given()
                .log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(getPhoneOtpDTO())
                .when()
                .post(baseUrl + Endpoints.SMS)
                .then()
                .log().all()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .response();

        SuccessfulResponse successfulResponse = response.as(SuccessfulResponse.class);
        assertThat(successfulResponse).isNotNull();
        assertThat(successfulResponse.getMessage()).isEqualTo(SuccessMessage.SUCCESS_OTP_MESSAGE);
    }

    @Test
    void testSendOtpViaCall() {
        Response response = given()
                .log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(getPhoneOtpDTO())
                .when()
                .post(baseUrl + Endpoints.CALL)
                .then()
                .log().all()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .response();

        SuccessfulResponse successfulResponse = response.as(SuccessfulResponse.class);
        assertThat(successfulResponse).isNotNull();
        assertThat(successfulResponse.getMessage()).isEqualTo(SuccessMessage.SUCCESS_OTP_MESSAGE);
    }

    @Test
    void testSendOtpViaEmailWithVerifyOtpAndStatusValidation() {
        Response response = given()
                .log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(getEmailOtpDTO())
                .when()
                .post(baseUrl + Endpoints.EMAIL)
                .then()
                .log().all()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .response();

        SuccessfulResponse successfulResponse = response.as(SuccessfulResponse.class);
        assertThat(successfulResponse).isNotNull();
        assertThat(successfulResponse.getMessage()).isEqualTo(SuccessMessage.SUCCESS_OTP_MESSAGE);

        List<OtpMessage> allByCustomerIdAndOtpStatus = otpRepository.findAllByCustomerIdAndOtpStatus(CUSTOMER_ID, OtpStatus.GENERATED);
        Optional<OtpMessage> first = allByCustomerIdAndOtpStatus.stream().findFirst();
        first.ifPresent(this::testVerifyOtp);
        testStatusOtp();
    }

    private void testVerifyOtp(OtpMessage otpMessage) {
        VerifyOtpDTO verifyOtpDTO = new VerifyOtpDTO(otpMessage.getCustomerId(), otpMessage.getOtp());
        Response response = given()
                .log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(verifyOtpDTO)
                .when()
                .put(baseUrl + Endpoints.VERIFY)
                .then()
                .log().all()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .response();

        SuccessfulResponse successfulResponse = response.as(SuccessfulResponse.class);
        assertThat(successfulResponse).isNotNull();
        assertThat(successfulResponse.getMessage()).isEqualTo(SuccessMessage.SUCCESS_OTP_VERIFICATION);
    }

    private void testStatusOtp() {
        Response response = given()
                .log().all()
                .when()
                .get(baseUrl + "/status/" + CUSTOMER_ID)
                .then()
                .log().all()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .response();

        ValidatedOtpDTO validatedOtpDTO = response.as(ValidatedOtpDTO.class);
        assertThat(validatedOtpDTO).isNotNull();
        assertThat(validatedOtpDTO.getMessage()).isEqualTo(SuccessMessage.SUCCESS_OTP_VALIDATION);
        assertThat(validatedOtpDTO.getLastValidatedTime()).isNotNull();
    }

    private BaseDTO getPhoneOtpDTO() {
        return BaseDTO.builder()
                .phoneNumber("+12345678901")
                .customerId(CUSTOMER_ID)
                .build();
    }

    private BaseDTO getEmailOtpDTO() {
        return BaseDTO.builder()
                .email("john.doe@example.com")
                .customerId(CUSTOMER_ID)
                .build();
    }
}
