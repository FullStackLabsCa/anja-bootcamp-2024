package io.reactivestax.active.life.canada.service;

import io.reactivestax.active.life.canada.constant.ExceptionMessage;
import io.reactivestax.active.life.canada.entity.FamilyCourseRegistration;
import io.reactivestax.active.life.canada.entity.FamilyMember;
import io.reactivestax.active.life.canada.entity.OfferedCourse;
import io.reactivestax.active.life.canada.exception.InvalidRequestException;
import io.reactivestax.active.life.canada.repository.FamilyCourseRegistrationRepository;
import io.reactivestax.active.life.canada.repository.FamilyMemberRepository;
import io.reactivestax.active.life.canada.repository.OfferedCourseRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class CourseRegistrationManagementService {

    private final FamilyCourseRegistrationRepository familyCourseRegistrationRepository;
    private final OfferedCourseRepository offeredCourseRepository;
    private final FamilyMemberRepository familyMemberRepository;

    public CourseRegistrationManagementService(FamilyCourseRegistrationRepository familyCourseRegistrationRepository,
                                               OfferedCourseRepository offeredCourseRepository,
                                               FamilyMemberRepository familyMemberRepository) {
        this.familyCourseRegistrationRepository = familyCourseRegistrationRepository;
        this.offeredCourseRepository = offeredCourseRepository;
        this.familyMemberRepository = familyMemberRepository;
    }

    public void enrollIntoOfferedCourse(String barCode, String memberLoginId) {
        OfferedCourse offeredCourse = offeredCourseRepository.findByBarCode(UUID.fromString(barCode))
                .orElseThrow(() -> new InvalidRequestException(ExceptionMessage.INVALID_OFFERED_COURSE_ID));
        FamilyMember familyMember = familyMemberRepository.findByMemberLoginId(memberLoginId)
                .orElseThrow(() -> new InvalidRequestException(ExceptionMessage.INVALID_MEMBER_ID));
        FamilyCourseRegistration familyCourseRegistration = FamilyCourseRegistration.builder()
                .build();
    }

    private void get(OfferedCourse offeredCourse, FamilyMember familyMember){
        offeredCourse.getFacility().getCity();
        familyMember.getCity();
    }
}
