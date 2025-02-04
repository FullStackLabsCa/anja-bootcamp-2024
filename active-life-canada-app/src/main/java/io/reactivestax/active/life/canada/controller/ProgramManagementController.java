package io.reactivestax.active.life.canada.controller;

import io.reactivestax.active.life.canada.constant.Endpoints;
import io.reactivestax.active.life.canada.constant.Message;
import io.reactivestax.active.life.canada.dto.*;
import io.reactivestax.active.life.canada.dto.group.CreateGroup;
import io.reactivestax.active.life.canada.service.ProgramManagementService;
import jakarta.validation.Valid;
import jakarta.validation.groups.Default;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(Endpoints.BASE_ENDPOINT)
@RequiredArgsConstructor
public class ProgramManagementController {

    private final ProgramManagementService programManagementService;

    @PostMapping(Endpoints.OFFERED_COURSES)
    public ResponseEntity<SuccessfulResponse> offerCourse(@Validated({CreateGroup.class, Default.class}) @RequestBody OfferCourseRequest offerCourseRequest) {
        this.programManagementService.offerCourse(offerCourseRequest);

        return ResponseEntity.ok(SuccessfulResponse.builder().message(Message.OFFERED_COURSE_ADDED).build());
    }

    @PutMapping(Endpoints.OFFERED_COURSES)
    public ResponseEntity<SuccessfulResponse> offeredCourse(@RequestBody CourseUpdateRequest courseUpdateRequest) {
        this.programManagementService.updateOfferedCourse(courseUpdateRequest);

        return ResponseEntity.ok(SuccessfulResponse.builder().message(Message.OFFERED_COURSE_UPDATED).build());
    }

    @GetMapping(Endpoints.OFFERED_COURSES)
    public ResponseEntity<List<OfferedCourseDetailsResponse>> offeredCourses() {

        return ResponseEntity.ok(this.programManagementService.offeredCourses());
    }

    @PostMapping(Endpoints.SEARCH_OFFERED_COURSES)
    public ResponseEntity<List<OfferedCourseDetailsResponse>> searchOfferedCourses(@RequestBody OfferedCourseSearchRequest offeredCourseSearchRequest) {
        List<OfferedCourseDetailsResponse> offeredCourseDetailsResponses = this.programManagementService.searchOfferedCourses(offeredCourseSearchRequest);

        return ResponseEntity.ok(offeredCourseDetailsResponses);
    }
}
