package io.reactivestax.active.life.canada.controller;


import io.reactivestax.active.life.canada.constant.Endpoints;
import io.reactivestax.active.life.canada.service.CourseRegistrationManagementService;
import org.springframework.web.bind.annotation.*;

@RestController
public class CourseRegistrationManagementController {

    private final CourseRegistrationManagementService courseRegistrationManagementService;

    public CourseRegistrationManagementController(CourseRegistrationManagementService courseRegistrationManagementService) {
        this.courseRegistrationManagementService = courseRegistrationManagementService;
    }

    @PostMapping(Endpoints.ENROLL_COURSE)
    public void enrollIntoCourse(@PathVariable String barCode, @PathVariable String memberLoginId) {

    }

    @GetMapping(Endpoints.REGISTERED_COURSES)
    public void registeredCourses() {

    }

    @GetMapping(Endpoints.WAITLISTED_COURSES)
    public void waitlistedCourses() {

    }

    @DeleteMapping(Endpoints.WITHDRAW_FROM_COURSE)
    public void withdrawFromCourse() {

    }
}
