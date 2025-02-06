package io.reactivestax.active.life.canada.service;

import io.reactivestax.active.life.canada.constant.ExceptionHandlerConst;
import io.reactivestax.active.life.canada.constant.Message;
import io.reactivestax.active.life.canada.dto.CartDto;
import io.reactivestax.active.life.canada.dto.FamilyCourseRegistrationDetails;
import io.reactivestax.active.life.canada.dto.OfferedCourseWaitlistDto;
import io.reactivestax.active.life.canada.entity.FamilyCourseRegistration;
import io.reactivestax.active.life.canada.entity.FamilyMember;
import io.reactivestax.active.life.canada.entity.OfferedCourse;
import io.reactivestax.active.life.canada.entity.OfferedCourseWaitlist;
import io.reactivestax.active.life.canada.enums.AvailableForEnrollment;
import io.reactivestax.active.life.canada.exception.InvalidRequestException;
import io.reactivestax.active.life.canada.exception.UnauthorizedAccessException;
import io.reactivestax.active.life.canada.mapper.FamilyCourseRegistrationMapper;
import io.reactivestax.active.life.canada.mapper.OfferedCourseWaitlistMapper;
import io.reactivestax.active.life.canada.repository.FamilyCourseRegistrationRepository;
import io.reactivestax.active.life.canada.repository.FamilyMemberRepository;
import io.reactivestax.active.life.canada.repository.OfferedCourseRepository;
import io.reactivestax.active.life.canada.repository.OfferedCourseWaitlistRepository;
import io.reactivestax.active.life.canada.util.ActiveLifeUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CourseRegistrationManagementService {

    private final FamilyCourseRegistrationRepository familyCourseRegistrationRepository;
    private final OfferedCourseRepository offeredCourseRepository;
    private final FamilyMemberRepository familyMemberRepository;
    private final OfferedCourseWaitlistRepository offeredCourseWaitlistRepository;
    private final FamilyCourseRegistrationMapper familyCourseRegistrationMapper;
    private final OfferedCourseWaitlistMapper offeredCourseWaitlistMapper;
    private final ActiveLifeUtil activeLifeUtil;
    private final AsyncJobsService asyncJobsService;
    private final CacheService cacheService;

    @Transactional
    public void addToCart(CartDto cartDto, String loggedInMemberId) {
        FamilyMember loggedInMember = familyMemberRepository.findByFamilyMemberIdAndIsActive(UUID.fromString(loggedInMemberId), true)
                .orElseThrow(() -> new UnauthorizedAccessException(ExceptionHandlerConst.UNAUTHORIZED_ACCESS));
        FamilyMember familyMember = familyMemberRepository.findByMemberLoginIdAndIsActiveAndFamilyGroup_FamilyGroupId
                        (cartDto.getFamilyMemberLoginId(), true, loggedInMember.getFamilyGroup().getFamilyGroupId())
                .orElseThrow(() -> new InvalidRequestException(ExceptionHandlerConst.INVALID_MEMBER_ID));
        OfferedCourse offeredCourse = offeredCourseRepository.findByBarCode(UUID.fromString(cartDto.getOfferedCourseBarCode()))
                .orElseThrow(() -> new InvalidRequestException(ExceptionHandlerConst.INVALID_OFFERED_COURSE_ID));
        if (familyCourseRegistrationRepository.existsByFamilyMember_FamilyMemberIdAndOfferedCourse_OfferedCourseIdAndIsWithdrawn
                (familyMember.getFamilyMemberId(), offeredCourse.getOfferedCourseId(), false))
            throw new InvalidRequestException(ExceptionHandlerConst.ALREADY_ENROLLED);
        switch (offeredCourse.getAvailableForEnrollment()) {
            case AVAILABLE -> asyncJobsService.addToCartCache(offeredCourse, familyMember, loggedInMemberId);
            case WAITLIST_OPEN -> throw new InvalidRequestException(ExceptionHandlerConst.ADD_TO_CART_FAILED_WAITLIST);
            case NOT_AVAILABLE ->
                    throw new InvalidRequestException(ExceptionHandlerConst.ADD_TO_CART_FAILED_NOT_AVAILABLE);
        }
    }

    private String addToWaitlist(OfferedCourse offeredCourse, FamilyMember familyMember, UUID enrollmentActorID) {
        if (offeredCourseWaitlistRepository.existsByFamilyMember_FamilyMemberIdAndOfferedCourse_OfferedCourseId
                (familyMember.getFamilyMemberId(), offeredCourse.getOfferedCourseId()))
            throw new InvalidRequestException(ExceptionHandlerConst.ALREADY_WAITLISTED);
        List<OfferedCourseWaitlist> courseWaitlist = offeredCourse.getOfferedCourseWaitlist();
        OfferedCourseWaitlist offeredCourseWaitlist = OfferedCourseWaitlist.builder()
                .enrollmentActorId(enrollmentActorID)
                .offeredCourse(offeredCourse)
                .familyMember(familyMember)
                .build();
        courseWaitlist.add(offeredCourseWaitlist);
        if (offeredCourse.getNoOfSpots() == courseWaitlist.size())
            offeredCourse.setAvailableForEnrollment(AvailableForEnrollment.NOT_AVAILABLE);
        offeredCourseRepository.save(offeredCourse);
        return Message.ADDED_TO_WAITLIST;
    }

    public List<FamilyCourseRegistrationDetails> getRegisteredCourses(String loggedInMemberId) {
        UUID loggedInMemberIdUUID = UUID.fromString(loggedInMemberId);
        if (!familyMemberRepository.existsByFamilyMemberIdAndIsActive(loggedInMemberIdUUID, true))
            throw new UnauthorizedAccessException(ExceptionHandlerConst.UNAUTHORIZED_ACCESS);
        List<FamilyCourseRegistration> familyCourseRegistrationList = familyCourseRegistrationRepository
                .findAllByEnrollmentActorIdOrFamilyMember_FamilyMemberId(loggedInMemberIdUUID, loggedInMemberIdUUID);
        return familyCourseRegistrationMapper.toDtoList(familyCourseRegistrationList);
    }

    public List<OfferedCourseWaitlistDto> getWaitlistedCourses(String loggedInMemberId) {
        UUID loggedInMemberIdUUID = UUID.fromString(loggedInMemberId);
        if (!familyMemberRepository.existsByFamilyMemberIdAndIsActive(loggedInMemberIdUUID, true))
            throw new UnauthorizedAccessException(ExceptionHandlerConst.UNAUTHORIZED_ACCESS);
        List<OfferedCourseWaitlist> offeredCourseWaitlist = offeredCourseWaitlistRepository
                .findAllByEnrollmentActorIdOrFamilyMember_FamilyMemberId(loggedInMemberIdUUID, loggedInMemberIdUUID);
        return offeredCourseWaitlistMapper.toDtoList(offeredCourseWaitlist);
    }

    @Transactional
    public void withdrawFromCourse(String familyCourseRegistrationId, String loggedInMemberId) {
        UUID loggedInMemberIdUUID = UUID.fromString(loggedInMemberId);
        if (!familyMemberRepository.existsByFamilyMemberIdAndIsActive(loggedInMemberIdUUID, true))
            throw new UnauthorizedAccessException(ExceptionHandlerConst.UNAUTHORIZED_ACCESS);
        FamilyCourseRegistration familyCourseRegistration = familyCourseRegistrationRepository
                .findByFamilyCourseRegistrationIdAndIsWithdrawnFalseAndEnrollmentActorIdOrFamilyMember_FamilyMemberId(
                        UUID.fromString(familyCourseRegistrationId), loggedInMemberIdUUID, loggedInMemberIdUUID)
                .orElseThrow(() -> new InvalidRequestException(ExceptionHandlerConst.INVALID_FAMILY_COURSE_REGISTRATION_ID));
        OfferedCourse offeredCourse = familyCourseRegistration.getOfferedCourse();
        if (activeLifeUtil.compareDateAndTime(offeredCourse.getStartDate(), offeredCourse.getStartTime()))
            throw new InvalidRequestException(ExceptionHandlerConst.WITHDRAW_NOT_ALLOWED);

        familyCourseRegistration.setIsWithdrawn(true);
        FamilyCourseRegistration savedFamilyCourseRegistration = familyCourseRegistrationRepository.save(familyCourseRegistration);
        asyncJobsService.updateWithDrawnCreditsInFamilyGroup(savedFamilyCourseRegistration);
        asyncJobsService.getAllWaitlistedMembersByOfferedCourseIdAndSendToEms(offeredCourse.getOfferedCourseId(),
                offeredCourse.getCourse().getName());
    }
}
