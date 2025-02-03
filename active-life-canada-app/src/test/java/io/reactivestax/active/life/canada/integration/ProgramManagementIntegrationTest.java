package io.reactivestax.active.life.canada.integration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.reactivestax.active.life.canada.constant.Endpoints;
import io.reactivestax.active.life.canada.constant.Message;
import io.reactivestax.active.life.canada.dto.CourseUpdateRequest;
import io.reactivestax.active.life.canada.dto.OfferCourseRequest;
import io.reactivestax.active.life.canada.dto.SuccessfulResponse;
import io.reactivestax.active.life.canada.entity.OfferedCourse;
import io.reactivestax.active.life.canada.repository.OfferedCourseRepository;
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
import java.time.LocalTime;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ProgramManagementIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private ObjectMapper objectMapper;

    private String baseUrl;

    @Autowired
    private OfferedCourseRepository offeredCourseRepository;

    @BeforeAll
    void setup() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = port;
        baseUrl = "http://localhost:" + port + Endpoints.BASE_ENDPOINT;
    }

    @Test
    void testProgramManagement() throws JsonProcessingException {
        testCreateOfferCourse();
        testUpdateOfferedCourse();
    }

    private void testCreateOfferCourse() throws JsonProcessingException {
        OfferCourseRequest offerCourseRequest = OfferCourseRequest.builder()
                .courseId(1L)
                .facilityId(1L)
                .noOfSpots(10)
                .startDate(LocalDate.now().plusDays(2))
                .endDate(LocalDate.now().plusDays(10))
                .startTime(LocalTime.now())
                .endTime(LocalTime.now().plusHours(2))
                .isAllDayCourse(true)
                .registrationStartDate(LocalDate.now())
                .residentCourseFee(180)
                .nonResidentCourseFee(200)
                .build();

        Response response = given()
                .log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(objectMapper.writeValueAsString(offerCourseRequest))
                .when()
                .post(baseUrl + Endpoints.OFFERED_COURSES)
                .then()
                .log().all()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .response();

        SuccessfulResponse successfulResponse = response.as(SuccessfulResponse.class);
        assertThat(successfulResponse).isNotNull();
        assertThat(successfulResponse.getMessage()).isEqualTo(Message.OFFERED_COURSE_ADDED);
    }

    private void testUpdateOfferedCourse() throws JsonProcessingException {
        OfferedCourse offeredCourse = offeredCourseRepository.findAll().get(0);

        CourseUpdateRequest courseUpdateRequest = CourseUpdateRequest.builder()
                .barCode(offeredCourse.getBarCode().toString())
                .isAllDayCourse(false)
                .build();

        Response response = given()
                .log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(objectMapper.writeValueAsString(courseUpdateRequest))
                .when()
                .put(baseUrl + Endpoints.OFFERED_COURSES)
                .then()
                .log().all()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .response();

        SuccessfulResponse successfulResponse = response.as(SuccessfulResponse.class);
        assertThat(successfulResponse).isNotNull();
        assertThat(successfulResponse.getMessage()).isEqualTo(Message.OFFERED_COURSE_UPDATED);
    }
}
