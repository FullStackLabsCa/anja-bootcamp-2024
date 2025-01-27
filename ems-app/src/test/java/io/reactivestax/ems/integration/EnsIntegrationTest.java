package io.reactivestax.ems.integration;

import io.reactivestax.ems.constant.Endpoints;
import io.reactivestax.ems.constant.SuccessMessage;
import io.reactivestax.ems.dto.BaseDTO;
import io.reactivestax.ems.dto.SuccessfulResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
class EnsIntegrationTest {

    private static final String BASE_URL = "http://localhost:8080" + Endpoints.ENS_BASE;

    @Test
    void testSendEnsMessageViaSms() {
        Response response = given()
                .log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(getEnsPhoneDTO())
                .when()
                .post(BASE_URL + Endpoints.SMS)
                .then()
                .log().all()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .response();

        SuccessfulResponse successfulResponse = response.as(SuccessfulResponse.class);
        assertThat(successfulResponse).isNotNull();
        assertThat(successfulResponse.getMessage()).isEqualTo(SuccessMessage.SUCCESS_ENS_MESSAGE);
    }

    @Test
    void testSendEnsMessageViaCall() {
        Response response = given()
                .log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(getEnsPhoneDTO())
                .when()
                .post(BASE_URL + Endpoints.CALL)
                .then()
                .log().all()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .response();

        SuccessfulResponse successfulResponse = response.as(SuccessfulResponse.class);
        assertThat(successfulResponse).isNotNull();
        assertThat(successfulResponse.getMessage()).isEqualTo(SuccessMessage.SUCCESS_ENS_MESSAGE);
    }

    @Test
    void testSendEnsMessageViaEmail() {
        Response response = given()
                .log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(getEnsEmailDTO())
                .when()
                .post(BASE_URL + Endpoints.EMAIL)
                .then()
                .log().all()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .response();

        SuccessfulResponse successfulResponse = response.as(SuccessfulResponse.class);
        assertThat(successfulResponse).isNotNull();
        assertThat(successfulResponse.getMessage()).isEqualTo(SuccessMessage.SUCCESS_ENS_MESSAGE);
    }

    private BaseDTO getEnsPhoneDTO() {
        return BaseDTO.builder()
                .customerId("f39b73ca-d6d9-4ee7-b6ff-7fd3c20fae85")
                .phoneNumber("+12266985174")
                .message("message")
                .build();
    }

    private BaseDTO getEnsEmailDTO() {
        return BaseDTO.builder()
                .customerId("f39b73ca-d6d9-4ee7-b6ff-7fd3c20fae85")
                .email("jainanant36@gmail.com")
                .message("message")
                .build();
    }
}
