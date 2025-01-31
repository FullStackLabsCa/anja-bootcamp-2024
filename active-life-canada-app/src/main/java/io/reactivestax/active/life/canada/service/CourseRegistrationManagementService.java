package io.reactivestax.active.life.canada.service;

import io.reactivestax.active.life.canada.constant.ExceptionMessage;
import io.reactivestax.active.life.canada.entity.FamilyCourseRegistration;
import io.reactivestax.active.life.canada.entity.FamilyMember;
import io.reactivestax.active.life.canada.entity.OfferedCourse;
import io.reactivestax.active.life.canada.entity.OfferedCourseFee;
import io.reactivestax.active.life.canada.enums.FeeType;
import io.reactivestax.active.life.canada.exception.InvalidRequestException;
import io.reactivestax.active.life.canada.exception.UnauthorizedException;
import io.reactivestax.active.life.canada.repository.FamilyCourseRegistrationRepository;
import io.reactivestax.active.life.canada.repository.FamilyMemberRepository;
import io.reactivestax.active.life.canada.repository.OfferedCourseRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
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

    @Transactional
    public void enrollIntoOfferedCourse(String barCode, String memberLoginId, String loggedInMemberId) {
        FamilyMember loggedInMember = familyMemberRepository.findById(UUID.fromString(loggedInMemberId))
                .orElseThrow(() -> new UnauthorizedException(ExceptionMessage.UNAUTHORIZED_ACCESS));
        OfferedCourse offeredCourse = offeredCourseRepository.findByBarCode(UUID.fromString(barCode))
                .orElseThrow(() -> new InvalidRequestException(ExceptionMessage.INVALID_OFFERED_COURSE_ID));
        FamilyMember familyMember = familyMemberRepository.findByMemberLoginId(memberLoginId)
                .orElseThrow(() -> new InvalidRequestException(ExceptionMessage.INVALID_MEMBER_ID));
        if (loggedInMember.getFamilyGroup().getFamilyGroupId().equals(familyMember.getFamilyGroup().getFamilyGroupId())) {
            FamilyCourseRegistration familyCourseRegistration = FamilyCourseRegistration.builder()
                    .offeredCourse(offeredCourse)
                    .familyMember(familyMember)
                    .cost(getFees(offeredCourse, familyMember).getCourseFee())
                    .isWithdrawn(false)
                    .withdrawnCredits(0)
                    .enrollmentActorId(loggedInMember.getFamilyMemberId())
                    .build();
            familyCourseRegistrationRepository.save(familyCourseRegistration);
        } else throw new InvalidRequestException(ExceptionMessage.INVALID_MEMBER_ID);
    }

    private OfferedCourseFee getFees(OfferedCourse offeredCourse, FamilyMember familyMember) {
        if (offeredCourse.getFacility().getCity().equals(familyMember.getCity())) {
            return offeredCourse.getOfferedCourseFees().stream()
                    .filter(offeredCourseFee -> offeredCourseFee.getFeeType().equals(FeeType.RESIDENT))
                    .findFirst().orElseThrow(() -> new InvalidRequestException(ExceptionMessage.RESIDENT_COURSE_FEE_NOT_FOUND));
        }
        return offeredCourse.getOfferedCourseFees().stream()
                .filter(offeredCourseFee -> offeredCourseFee.getFeeType().equals(FeeType.NON_RESIDENT))
                .findFirst().orElseThrow(() -> new InvalidRequestException(ExceptionMessage.NON_RESIDENT_COURSE_FEE_NOT_FOUND));
    }

    public List<FamilyCourseRegistration> getWaitlistedCourses(String loggedInMemberId){
        FamilyMember familyMember = familyMemberRepository.findById(UUID.fromString(loggedInMemberId))
                .orElseThrow(() -> new UnauthorizedException(ExceptionMessage.UNAUTHORIZED_ACCESS));
        return familyCourseRegistrationRepository.findAllByFamilyMemberIdOrEnrollmentActorId(familyMember.getFamilyMemberId(), loggedInMemberId);
    }

    @Transactional
    public void withdrawFromCourse(String familyCourseRegistrationId, String loggedInMemberId) {
        FamilyMember familyMember = familyMemberRepository.findById(UUID.fromString(loggedInMemberId))
                .orElseThrow(() -> new UnauthorizedException(ExceptionMessage.UNAUTHORIZED_ACCESS));
        FamilyCourseRegistration familyCourseRegistration = familyCourseRegistrationRepository
                .findByFamilyCourseRegistrationIdAndEnrollmentActorIdOrFamilyMemberIdForNonWithdrawnCourse(
                        UUID.fromString(familyCourseRegistrationId), familyMember.getFamilyMemberId())
                .orElseThrow(() -> new InvalidRequestException(ExceptionMessage.INVALID_FAMILY_COURSE_REGISTRATION_ID));
        LocalDate startDate = familyCourseRegistration.getOfferedCourse().getStartDate();
        long between = ChronoUnit.DAYS.between(startDate, LocalDate.now());
        if (between > 2) {
            throw new InvalidRequestException(ExceptionMessage.WITHDRAW_NOT_ALLOWED);
        }
        familyCourseRegistration.setIsWithdrawn(true);
        familyCourseRegistrationRepository.save(familyCourseRegistration);
    }
}
