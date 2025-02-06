package io.reactivestax.active.life.canada.service;

import io.reactivestax.active.life.canada.constant.ExceptionHandlerConst;
import io.reactivestax.active.life.canada.constant.Message;
import io.reactivestax.active.life.canada.dto.CartDto;
import io.reactivestax.active.life.canada.dto.CartResponse;
import io.reactivestax.active.life.canada.dto.FamilyCourseRegistrationDetails;
import io.reactivestax.active.life.canada.dto.OfferedCourseWaitlistDto;
import io.reactivestax.active.life.canada.entity.*;
import io.reactivestax.active.life.canada.enums.AvailableForEnrollment;
import io.reactivestax.active.life.canada.enums.FeeType;
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
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
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
            case AVAILABLE -> addToCartCache(loggedInMemberId, cartDto);
            case WAITLIST_OPEN -> throw new InvalidRequestException(ExceptionHandlerConst.ADD_TO_CART_FAILED_WAITLIST);
            case NOT_AVAILABLE ->
                    throw new InvalidRequestException(ExceptionHandlerConst.ADD_TO_CART_FAILED_NOT_AVAILABLE);
        }
    }

    public void addToCartCache(String enrollmentActorID, CartDto cartDto) {
        List<CartDto> cartDtoList = cacheService.addToCache(enrollmentActorID, cartDto);
        log.info("Cache size for member - {} is {}", enrollmentActorID, cartDtoList.size());
    }

    public List<CartResponse> getCart(String loggedInMemberId) {
        if (familyMemberRepository.existsByFamilyMemberIdAndIsActive(UUID.fromString(loggedInMemberId), true)) {
            List<CartDto> cart = cacheService.getCart(loggedInMemberId);
            return cart.stream().map(cartDto -> {
                CartResponse cartResponse = new CartResponse();
                offeredCourseRepository.findByBarCode(UUID.fromString(cartDto.getOfferedCourseBarCode())).ifPresent(offeredCourse -> {
                    familyMemberRepository.findByMemberLoginId(cartDto.getFamilyMemberLoginId()).ifPresent(familyMember -> {
                        cartResponse.setCourseName(offeredCourse.getCourse().getName());
                        cartResponse.setStatus(offeredCourse.getAvailableForEnrollment());
                        cartResponse.setFee(getFees(offeredCourse, familyMember).getCourseFee());
                    });
                });
                return cartResponse;
            }).toList();
        } else throw new UnauthorizedAccessException(ExceptionHandlerConst.UNAUTHORIZED_ACCESS);
    }

    private OfferedCourseFee getFees(OfferedCourse offeredCourse, FamilyMember familyMember) {
        FeeType feeType;
        String exceptionMessage;
        if (offeredCourse.getFacility().getCity().equals(familyMember.getCity())) {
            feeType = FeeType.RESIDENT;
            exceptionMessage = ExceptionHandlerConst.RESIDENT_COURSE_FEE_NOT_FOUND;
        } else {
            feeType = FeeType.NON_RESIDENT;
            exceptionMessage = ExceptionHandlerConst.NON_RESIDENT_COURSE_FEE_NOT_FOUND;
        }

        return offeredCourse.getOfferedCourseFees().stream()
                .filter(offeredCourseFee -> offeredCourseFee.getFeeType().equals(feeType))
                .findFirst().orElseThrow(() -> new InvalidRequestException(exceptionMessage));
    }

    public void payForCart(String loggedInMemberId) {
//        if (familyMemberRepository.existsByFamilyMemberIdAndIsActive(UUID.fromString(loggedInMemberId), true)) {
//            List<CartDto> cart = cacheService.getCart(loggedInMemberId);
//            if (!cart.isEmpty()) {
//                cart.stream().map(cartDto -> {
//                    familyMemberRepository.findByMemberLoginIdAndIsActive(cartDto.getFamilyMemberLoginId(), true).ifPresent(familyMember -> {
//                       offeredCourseRepository.findByBarCode(UUID.fromString(cartDto.getOfferedCourseBarCode()))
//                    });
//                });
//            } else throw new InvalidRequestException(ExceptionHandlerConst.EMPTY_CART);
//        } else throw new UnauthorizedAccessException(ExceptionHandlerConst.UNAUTHORIZED_ACCESS);
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
