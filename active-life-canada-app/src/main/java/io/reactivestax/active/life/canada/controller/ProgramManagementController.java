package io.reactivestax.active.life.canada.controller;

import io.reactivestax.active.life.canada.constant.Endpoints;
import io.reactivestax.active.life.canada.dto.OfferCourseRequest;
import io.reactivestax.active.life.canada.service.ProgramManagementService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(Endpoints.BASE_ENDPOINT)
public class ProgramManagementController {

    private final ProgramManagementService programManagementService;

    public ProgramManagementController(ProgramManagementService programManagementService) {
        this.programManagementService =programManagementService;
    }

    @PostMapping(Endpoints.OFFERED_COURSES)
    public void offerCourse(@RequestBody OfferCourseRequest offerCourseRequest) {
        this.programManagementService.offerCourse(offerCourseRequest);
    }

    @PutMapping(Endpoints.OFFERED_COURSES)
    public void offeredCourse() {

    }

    @GetMapping(Endpoints.OFFERED_COURSES)
    public void offeredCourses() {

    }

    @PostMapping(Endpoints.SEARCH_OFFERED_COURSES)
    public void searchOfferedCourses() {

    }

    @GetMapping(Endpoints.REGISTERED_COURSES)
    public void registeredCourses() {

    }

    @GetMapping(Endpoints.WAITLISTED_COURSES)
    public void waitlistedCourses() {

    }

    @PostMapping(Endpoints.ENROLL_COURSE)
    public void enrollIntoCourse() {

    }

    @DeleteMapping(Endpoints.WITHDRAW_FROM_COURSE)
    public void withdrawFromCourse() {

    }

    @PostMapping(Endpoints.OFFER_COURSES_BY_FACILITY)
    public void offerCourseByFacility() {

    }
}
