package io.reactivestax.ems.integration;

import io.reactivestax.ems.constant.Endpoints;
import io.reactivestax.ems.constant.SuccessMessage;
import io.reactivestax.ems.dto.BaseDTO;
import io.reactivestax.ems.dto.SuccessfulResponse;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class EnsIntegrationTest {

    @LocalServerPort
    private int port;

    private String baseUrl;

    private static final String CUSTOMER_ID = "6e6a7d4b-1eaa-4e23-9fd3-8b5d0840d3f1";

    @BeforeAll
    void setup() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = port;
        baseUrl = "http://localhost:" + port + Endpoints.ENS_BASE;
    }

    @Test
    void testSendEnsMessageViaSms() {
        Response response = given()
                .log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(getEnsPhoneDTO())
                .when()
                .post(baseUrl + Endpoints.SMS)
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
                .post(baseUrl + Endpoints.CALL)
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
                .post(baseUrl + Endpoints.EMAIL)
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
                .customerId(CUSTOMER_ID)
                .phoneNumber("+12345678901")
                .message("message")
                .build();
    }

    private BaseDTO getEnsEmailDTO() {
        return BaseDTO.builder()
                .customerId(CUSTOMER_ID)
                .email("john.doe@example.com")
                .message("message")
                .build();
    }
}
