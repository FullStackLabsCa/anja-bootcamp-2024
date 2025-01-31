package io.reactivestax.active.life.canada.controller;

import io.reactivestax.active.life.canada.constant.Endpoints;
import io.reactivestax.active.life.canada.constant.Message;
import io.reactivestax.active.life.canada.dto.CourseDetailsResponse;
import io.reactivestax.active.life.canada.dto.CourseUpdateRequest;
import io.reactivestax.active.life.canada.dto.OfferCourseRequest;
import io.reactivestax.active.life.canada.dto.SuccessfulResponse;
import io.reactivestax.active.life.canada.service.ProgramManagementService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(Endpoints.BASE_ENDPOINT)
public class ProgramManagementController {

    private final ProgramManagementService programManagementService;

    public ProgramManagementController(ProgramManagementService programManagementService) {
        this.programManagementService = programManagementService;
    }

    @PostMapping(Endpoints.OFFERED_COURSES)
    public ResponseEntity<SuccessfulResponse> offerCourse(@RequestBody OfferCourseRequest offerCourseRequest) {
        this.programManagementService.offerCourse(offerCourseRequest);

        return ResponseEntity.ok(SuccessfulResponse.builder().message(Message.OFFERED_COURSE_ADDED).build());
    }

    @PutMapping(Endpoints.OFFERED_COURSES)
    public ResponseEntity<SuccessfulResponse> offeredCourse(@RequestBody CourseUpdateRequest courseUpdateRequest) {
        this.programManagementService.updateOfferedCourse(courseUpdateRequest);

        return ResponseEntity.ok(SuccessfulResponse.builder().message(Message.OFFERED_COURSE_UPDATED).build());
    }

    @GetMapping(Endpoints.OFFERED_COURSES)
    public List<CourseDetailsResponse> offeredCourses() {
        return this.programManagementService.offeredCourses();
    }

    @PostMapping(Endpoints.SEARCH_OFFERED_COURSES)
    public void searchOfferedCourses() {

    }

    @PostMapping(Endpoints.OFFER_COURSES_BY_FACILITY)
    public void offerCourseByFacility() {

    }
}
