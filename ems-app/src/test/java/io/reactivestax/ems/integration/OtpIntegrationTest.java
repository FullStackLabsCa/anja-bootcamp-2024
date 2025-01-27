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
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.List;
import java.util.Optional;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
class OtpIntegrationTest {

    private static final String BASE_URL = "http://localhost:8080" + Endpoints.OTP_BASE;
    private static final String CUSTOMER_ID = "02e81b1a-f9ea-4da9-87ee-f6def2e15589";

    @Autowired
    private OtpRepository otpRepository;

    @Test
    void testSendOtpViaSms() {
        Response response = given()
                .log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(getPhoneOtpDTO())
                .when()
                .post(BASE_URL + Endpoints.SMS)
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
                .post(BASE_URL + Endpoints.CALL)
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
                .post(BASE_URL + Endpoints.EMAIL)
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
                .put(BASE_URL + Endpoints.VERIFY)
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
                .get(BASE_URL + "/status/" + CUSTOMER_ID)
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
                .phoneNumber("+12266985174")
                .customerId(CUSTOMER_ID)
                .build();
    }

    private BaseDTO getEmailOtpDTO() {
        return BaseDTO.builder()
                .email("jainanant36@gmail.com")
                .customerId(CUSTOMER_ID)
                .build();
    }
}
