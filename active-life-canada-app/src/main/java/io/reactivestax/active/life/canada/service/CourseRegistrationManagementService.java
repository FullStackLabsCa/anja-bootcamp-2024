package io.reactivestax.active.life.canada.service;

import io.reactivestax.active.life.canada.constant.ExceptionMessage;
import io.reactivestax.active.life.canada.constant.Message;
import io.reactivestax.active.life.canada.dto.FamilyCourseRegistrationDetails;
import io.reactivestax.active.life.canada.dto.OfferedCourseWaitlistDto;
import io.reactivestax.active.life.canada.entity.*;
import io.reactivestax.active.life.canada.enums.AvailableForEnrollment;
import io.reactivestax.active.life.canada.enums.FeeType;
import io.reactivestax.active.life.canada.exception.InvalidRequestException;
import io.reactivestax.active.life.canada.exception.UnauthorizedException;
import io.reactivestax.active.life.canada.mapper.FamilyCourseRegistrationMapper;
import io.reactivestax.active.life.canada.mapper.OfferedCourseWaitlistMapper;
import io.reactivestax.active.life.canada.repository.FamilyCourseRegistrationRepository;
import io.reactivestax.active.life.canada.repository.FamilyMemberRepository;
import io.reactivestax.active.life.canada.repository.OfferedCourseRepository;
import io.reactivestax.active.life.canada.repository.OfferedCourseWaitlistRepository;
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
    private final OfferedCourseWaitlistRepository offeredCourseWaitlistRepository;
    private final FamilyCourseRegistrationMapper familyCourseRegistrationMapper;
    private final OfferedCourseWaitlistMapper offeredCourseWaitlistMapper;

    public CourseRegistrationManagementService(FamilyCourseRegistrationRepository familyCourseRegistrationRepository,
                                               OfferedCourseRepository offeredCourseRepository,
                                               FamilyMemberRepository familyMemberRepository,
                                               OfferedCourseWaitlistRepository offeredCourseWaitlistRepository,
                                               FamilyCourseRegistrationMapper familyCourseRegistrationMapper,
                                               OfferedCourseWaitlistMapper offeredCourseWaitlistMapper) {
        this.familyCourseRegistrationRepository = familyCourseRegistrationRepository;
        this.offeredCourseRepository = offeredCourseRepository;
        this.familyMemberRepository = familyMemberRepository;
        this.offeredCourseWaitlistRepository = offeredCourseWaitlistRepository;
        this.familyCourseRegistrationMapper = familyCourseRegistrationMapper;
        this.offeredCourseWaitlistMapper = offeredCourseWaitlistMapper;
    }

    @Transactional
    public String enrollIntoOfferedCourse(String barCode, String memberLoginId, String loggedInMemberId) {
        String enrollmentMessage;
        FamilyMember loggedInMember = familyMemberRepository.findById(UUID.fromString(loggedInMemberId))
                .orElseThrow(() -> new UnauthorizedException(ExceptionMessage.UNAUTHORIZED_ACCESS));
        FamilyMember familyMember = familyMemberRepository.findByMemberLoginIdAndFamilyGroup_FamilyGroupId
                        (memberLoginId, loggedInMember.getFamilyGroup().getFamilyGroupId())
                .orElseThrow(() -> new InvalidRequestException(ExceptionMessage.INVALID_MEMBER_ID));
        OfferedCourse offeredCourse = offeredCourseRepository.findByBarCode(UUID.fromString(barCode))
                .orElseThrow(() -> new InvalidRequestException(ExceptionMessage.INVALID_OFFERED_COURSE_ID));
        if (offeredCourse.getAvailableForEnrollment().equals(AvailableForEnrollment.AVAILABLE)) {
            FamilyCourseRegistration familyCourseRegistration = FamilyCourseRegistration.builder()
                    .offeredCourse(offeredCourse)
                    .familyMember(familyMember)
                    .cost(getFees(offeredCourse, familyMember).getCourseFee())
                    .isWithdrawn(false)
                    .withdrawnCredits(0)
                    .enrollmentActorId(loggedInMember.getFamilyMemberId())
                    .build();
            List<FamilyCourseRegistration> familyCourseRegistrations = offeredCourse.getFamilyCourseRegistrations();
            familyCourseRegistrations.add(familyCourseRegistration);
            if (familyCourseRegistrations.size() == offeredCourse.getNoOfSpots()) {
                offeredCourse.setAvailableForEnrollment(AvailableForEnrollment.WAITLIST_OPEN);
            }
            offeredCourseRepository.save(offeredCourse);
            enrollmentMessage = Message.ENROLLMENT_SUCCESSFUL;
        } else if (offeredCourse.getAvailableForEnrollment().equals(AvailableForEnrollment.WAITLIST_OPEN)) {
            List<OfferedCourseWaitlist> courseWaitlist = offeredCourse.getOfferedCourseWaitlist();
            OfferedCourseWaitlist offeredCourseWaitlist = OfferedCourseWaitlist.builder()
                    .enrollmentActorId(UUID.fromString(loggedInMemberId))
                    .offeredCourse(offeredCourse)
                    .familyMember(familyMember)
                    .build();
            courseWaitlist.add(offeredCourseWaitlist);
            if (offeredCourse.getNoOfSpots() == courseWaitlist.size()) {
                offeredCourse.setAvailableForEnrollment(AvailableForEnrollment.NOT_AVAILABLE);
            }
            offeredCourseRepository.save(offeredCourse);
            enrollmentMessage = Message.ADDED_TO_WAITLIST;
        } else throw new InvalidRequestException(ExceptionMessage.COURSE_FULL);

        return enrollmentMessage;
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

    public List<FamilyCourseRegistrationDetails> getRegisteredCourses(String loggedInMemberId) {
        FamilyMember familyMember = familyMemberRepository.findById(UUID.fromString(loggedInMemberId))
                .orElseThrow(() -> new UnauthorizedException(ExceptionMessage.UNAUTHORIZED_ACCESS));
        List<FamilyCourseRegistration> familyCourseRegistrationList = familyCourseRegistrationRepository
                .findAllByEnrollmentActorIdOrFamilyMember_FamilyMemberId(UUID.fromString(loggedInMemberId),
                        familyMember.getFamilyMemberId());
        return familyCourseRegistrationMapper.toDtoList(familyCourseRegistrationList);
    }

    public List<OfferedCourseWaitlistDto> getWaitlistedCourses(String loggedInMemberId) {
        FamilyMember familyMember = familyMemberRepository.findById(UUID.fromString(loggedInMemberId))
                .orElseThrow(() -> new UnauthorizedException(ExceptionMessage.UNAUTHORIZED_ACCESS));
        List<OfferedCourseWaitlist> offeredCourseWaitlist = offeredCourseWaitlistRepository
                .findAllByEnrollmentActorIdOrFamilyMember_FamilyMemberId(UUID.fromString(loggedInMemberId),
                        familyMember.getFamilyMemberId());
        return offeredCourseWaitlistMapper.toDtoList(offeredCourseWaitlist);
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
