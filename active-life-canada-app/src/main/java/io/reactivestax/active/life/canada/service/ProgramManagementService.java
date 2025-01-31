package io.reactivestax.active.life.canada.service;

import io.reactivestax.active.life.canada.constant.ExceptionMessage;
import io.reactivestax.active.life.canada.dto.OfferCourseRequest;
import io.reactivestax.active.life.canada.entity.Course;
import io.reactivestax.active.life.canada.entity.Facility;
import io.reactivestax.active.life.canada.entity.OfferedCourse;
import io.reactivestax.active.life.canada.entity.OfferedCourseFee;
import io.reactivestax.active.life.canada.exception.InvalidRequestException;
import io.reactivestax.active.life.canada.mapper.OfferCourseMapper;
import io.reactivestax.active.life.canada.repository.CourseRepository;
import io.reactivestax.active.life.canada.repository.FacilityRepository;
import io.reactivestax.active.life.canada.repository.OfferedCourseFeeRepository;
import io.reactivestax.active.life.canada.repository.OfferedCourseRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class ProgramManagementService {

    private final OfferCourseMapper offerCourseMapper;
    private final CourseRepository courseRepository;
    private final FacilityRepository facilityRepository;
    private final OfferedCourseRepository offeredCourseRepository;
    private final OfferedCourseFeeRepository offeredCourseFeeRepository;

    public ProgramManagementService(OfferCourseMapper offerCourseMapper,
                                    CourseRepository courseRepository,
                                    FacilityRepository facilityRepository,
                                    OfferedCourseRepository offeredCourseRepository,
                                    OfferedCourseFeeRepository offeredCourseFeeRepository) {
        this.offerCourseMapper = offerCourseMapper;
        this.courseRepository = courseRepository;
        this.facilityRepository = facilityRepository;
        this.offeredCourseRepository = offeredCourseRepository;
        this.offeredCourseFeeRepository = offeredCourseFeeRepository;
    }

    @Transactional
    public void offerCourse(OfferCourseRequest offerCourseRequest) {
        Course course = courseRepository.findById(offerCourseRequest.getCourseId())
                .orElseThrow(() -> new InvalidRequestException(ExceptionMessage.INVALID_COURSE_ID));
        Facility facility = facilityRepository.findById(offerCourseRequest.getFacilityId())
                .orElseThrow(() -> new InvalidRequestException(ExceptionMessage.INVALID_COURSE_ID));
        OfferedCourse offeredCourse = offerCourseMapper.offerCourseRequestToOfferedCourse(offerCourseRequest);
        offeredCourse.setBarCode(UUID.randomUUID());
        offeredCourse.setCourse(course);
        offeredCourse.setFacility(facility);
        OfferedCourseFee offeredCourseFee = offerCourseMapper.offerCourseRequestToOfferedCourseFee(offerCourseRequest);
        offeredCourseFee.setOfferedCourse(offeredCourse);
        offeredCourseFeeRepository.save(offeredCourseFee);
    }
}
