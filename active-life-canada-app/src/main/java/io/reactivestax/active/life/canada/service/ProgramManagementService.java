package io.reactivestax.active.life.canada.service;

import io.reactivestax.active.life.canada.constant.ExceptionHandlerConst;
import io.reactivestax.active.life.canada.dto.CourseUpdateRequest;
import io.reactivestax.active.life.canada.dto.OfferCourseRequest;
import io.reactivestax.active.life.canada.dto.OfferedCourseDetailsResponse;
import io.reactivestax.active.life.canada.dto.OfferedCourseSearchRequest;
import io.reactivestax.active.life.canada.entity.Course;
import io.reactivestax.active.life.canada.entity.Facility;
import io.reactivestax.active.life.canada.entity.OfferedCourse;
import io.reactivestax.active.life.canada.entity.OfferedCourseFee;
import io.reactivestax.active.life.canada.enums.FeeType;
import io.reactivestax.active.life.canada.exception.InvalidRequestException;
import io.reactivestax.active.life.canada.mapper.OfferedCourseMapper;
import io.reactivestax.active.life.canada.repository.CourseRepository;
import io.reactivestax.active.life.canada.repository.FacilityRepository;
import io.reactivestax.active.life.canada.repository.OfferedCourseFeeRepository;
import io.reactivestax.active.life.canada.repository.OfferedCourseRepository;
import io.reactivestax.active.life.canada.specification.OfferedCourseSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProgramManagementService {

    private final OfferedCourseMapper offeredCourseMapper;
    private final CourseRepository courseRepository;
    private final FacilityRepository facilityRepository;
    private final OfferedCourseRepository offeredCourseRepository;
    private final OfferedCourseFeeRepository offeredCourseFeeRepository;
    private final ActiveLifeCommonService activeLifeCommonService;

    @Transactional
    public void offerCourse(OfferCourseRequest offerCourseRequest) {
        Course course = courseRepository.findById(offerCourseRequest.getCourseId())
                .orElseThrow(() -> new InvalidRequestException(ExceptionHandlerConst.INVALID_COURSE_ID));
        Facility facility = facilityRepository.findById(offerCourseRequest.getFacilityId())
                .orElseThrow(() -> new InvalidRequestException(ExceptionHandlerConst.INVALID_FACILITY_ID));
        OfferedCourse offeredCourse = offeredCourseMapper.offerCourseRequestToOfferedCourse(offerCourseRequest);
        offeredCourse.setBarCode(UUID.randomUUID());
        offeredCourse.setCourse(course);
        offeredCourse.setFacility(facility);
        List<OfferedCourseFee> offeredCourseFeeList = new ArrayList<>();
        offeredCourseFeeList.add(OfferedCourseFee.builder()
                .feeType(FeeType.RESIDENT)
                .offeredCourse(offeredCourse)
                .courseFee(offerCourseRequest.getResidentCourseFee()).build());
        offeredCourseFeeList.add(OfferedCourseFee.builder()
                .feeType(FeeType.NON_RESIDENT)
                .offeredCourse(offeredCourse)
                .courseFee(offerCourseRequest.getNonResidentCourseFee()).build());
        offeredCourseFeeRepository.saveAll(offeredCourseFeeList);
    }

    @Transactional
    public void updateOfferedCourse(CourseUpdateRequest courseUpdateRequest) {
        String barCode = courseUpdateRequest.getBarCode();
        OfferedCourse offeredCourse = offeredCourseRepository.findByBarCode(UUID.fromString(barCode))
                .orElseThrow(() -> new InvalidRequestException(ExceptionHandlerConst.INVALID_OFFERED_COURSE_ID));
        Integer noOfSpots = offeredCourse.getNoOfSpots();
        offeredCourseMapper.updateOfferedCourseRequestToOfferedCourse(courseUpdateRequest, offeredCourse);
        offeredCourseRepository.save(offeredCourse);
        if (offeredCourse.getNoOfSpots() > noOfSpots)
            activeLifeCommonService.getAllWaitlistedMembersByOfferedCourseIdAndSendToEms(offeredCourse.getOfferedCourseId(),
                    offeredCourse.getCourse().getName());
    }

    public List<OfferedCourseDetailsResponse> offeredCourses() {
        List<OfferedCourse> offeredCourses = offeredCourseRepository.findAll();
        return offeredCourseMapper.offeredCoursesToListOfCourseDetails(offeredCourses);
    }

    public List<OfferedCourseDetailsResponse> searchOfferedCourses(OfferedCourseSearchRequest offeredCourseSearchRequest) {
        Specification<OfferedCourse> offeredCourseSpecification = Specification
                .where(OfferedCourseSpecification.hasCourseName(offeredCourseSearchRequest.getCourseName()))
                .and(OfferedCourseSpecification.hasStartDate(offeredCourseSearchRequest.getStartDate()))
                .and(OfferedCourseSpecification.hasEndDate(offeredCourseSearchRequest.getEndDate()))
                .and(OfferedCourseSpecification.hasCity(offeredCourseSearchRequest.getCity()))
                .and(OfferedCourseSpecification.hasProvince(offeredCourseSearchRequest.getProvince()))
                .and(OfferedCourseSpecification.hasCategory(offeredCourseSearchRequest.getCategory()))
                .and(OfferedCourseSpecification.hasSubCategory(offeredCourseSearchRequest.getSubCategory()))
                .and(OfferedCourseSpecification.hasAgeGroup(offeredCourseSearchRequest.getAgeGroup()));
        List<OfferedCourse> offeredCourseList = offeredCourseRepository.findAll(offeredCourseSpecification);

        return offeredCourseMapper.offeredCoursesToListOfCourseDetails(offeredCourseList);
    }
}
