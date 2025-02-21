package io.reactivestax.active.life.canada.service;

import io.reactivestax.active.life.canada.constant.ExceptionHandlerConst;
import io.reactivestax.active.life.canada.dto.*;
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

import java.text.MessageFormat;
import java.time.LocalDate;
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
    private final PaymentService paymentService;

    @Transactional
    public void addToCart(CourseEnrollmentWaitlistDto cartDto, String loggedInMemberId) {
        FamilyMember loggedInMember = checkUnauthorizedAccess(loggedInMemberId);
        FamilyMember familyMember = familyMemberRepository.findByMemberLoginIdAndIsActiveAndFamilyGroup_FamilyGroupId
                        (cartDto.getFamilyMemberLoginId(), true, loggedInMember.getFamilyGroup().getFamilyGroupId())
                .orElseThrow(() -> new InvalidRequestException(ExceptionHandlerConst.INVALID_MEMBER_ID));
        OfferedCourse offeredCourse = offeredCourseRepository.findByBarCode(UUID.fromString(cartDto.getOfferedCourseBarCode()))
                .orElseThrow(() -> new InvalidRequestException(ExceptionHandlerConst.INVALID_OFFERED_COURSE_ID));
        if (familyCourseRegistrationRepository.existsByFamilyMember_FamilyMemberIdAndOfferedCourse_OfferedCourseIdAndIsWithdrawn
                (familyMember.getFamilyMemberId(), offeredCourse.getOfferedCourseId(), false))
            throw new InvalidRequestException(ExceptionHandlerConst.ALREADY_ENROLLED);
        switch (offeredCourse.getAvailableForEnrollment()) {
            case AVAILABLE -> addToCartCache(loggedInMember.getFamilyMemberId().toString(), cartDto);
            case WAITLIST_OPEN -> throw new InvalidRequestException(ExceptionHandlerConst.ADD_TO_CART_FAILED_WAITLIST);
            case NOT_AVAILABLE ->
                    throw new InvalidRequestException(ExceptionHandlerConst.ADD_TO_CART_FAILED_NOT_AVAILABLE);
        }
    }

    private void addToCartCache(String enrollmentActorID, CourseEnrollmentWaitlistDto cartDto) {
        List<CourseEnrollmentWaitlistDto> cartDtoList = cacheService.addToCache(enrollmentActorID, cartDto);
        log.info("Cache size for member - {} is {}", enrollmentActorID, cartDtoList.size());
    }

    public List<CartResponse> getCart(String loggedInMemberLoginId) {
        FamilyMember loggedInFamilyMember = checkUnauthorizedAccess(loggedInMemberLoginId);
        String loggedInMemberId = loggedInFamilyMember.getFamilyMemberId().toString();
        List<CourseEnrollmentWaitlistDto> cart = cacheService.getCart(loggedInMemberId);
        return cart.stream().map(cartDto -> {
            CartResponse cartResponse = new CartResponse();
            offeredCourseRepository.findByBarCode(UUID.fromString(cartDto.getOfferedCourseBarCode())).ifPresent(offeredCourse ->
                    familyMemberRepository.findByMemberLoginId(cartDto.getFamilyMemberLoginId()).ifPresent(familyMember -> {
                        cartResponse.setCourseName(offeredCourse.getCourse().getName());
                        cartResponse.setStatus(offeredCourse.getAvailableForEnrollment());
                        cartResponse.setFee(getFees(offeredCourse, familyMember).getCourseFee());
                    }));
            return cartResponse;
        }).toList();
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

    public void payForCart(String loggedInMemberLoginId, PaymentDto paymentDto) {
        FamilyMember loggedInFamilyMember = checkUnauthorizedAccess(loggedInMemberLoginId);
        String loggedInMemberId = loggedInFamilyMember.getFamilyMemberId().toString();
        List<CourseEnrollmentWaitlistDto> cart = cacheService.getCart(loggedInMemberId);
        if (!cart.isEmpty()) {
            List<FamilyCourseRegistration> familyCourseRegistrationList = cart.stream().map(cartDto -> {
                FamilyMember familyMember = familyMemberRepository.findByMemberLoginIdAndIsActive
                        (cartDto.getFamilyMemberLoginId(), true).orElseThrow();
                OfferedCourse offeredCourse = offeredCourseRepository.findByBarCodeAndAvailableForEnrollment
                                (UUID.fromString(cartDto.getOfferedCourseBarCode()), AvailableForEnrollment.AVAILABLE)
                        .orElseThrow(() -> new InvalidRequestException
                                (MessageFormat.format(ExceptionHandlerConst.COURSE_NO_LONGER_AVAILABLE, cartDto.getOfferedCourseBarCode())));
                checkIfRegistrationExists(offeredCourse, familyMember);
                FamilyCourseRegistration familyCourseRegistration = getFamilyCourseRegistrationEntity
                        (offeredCourse, familyMember, loggedInMemberId);
                asyncJobsService.checkAndUpdateCourseAvailabilityToWaitlist(offeredCourse);
                asyncJobsService.removeEntryFromWaitlistIfExists(offeredCourse.getOfferedCourseId(), familyMember.getFamilyMemberId());
                return familyCourseRegistration;
            }).toList();
            int totalCost = familyCourseRegistrationList.stream()
                    .mapToInt(FamilyCourseRegistration::getCost).sum();
            paymentService.createPaymentIntentAndConfirm(totalCost, paymentDto.getPaymentMethodId());
            familyCourseRegistrationRepository.saveAll(familyCourseRegistrationList);
            cacheService.clearCart(loggedInMemberId);
        } else throw new InvalidRequestException(ExceptionHandlerConst.EMPTY_CART);
    }

    private void checkIfRegistrationExists(OfferedCourse offeredCourse, FamilyMember familyMember) {
        if (familyCourseRegistrationRepository.existsByFamilyMember_FamilyMemberIdAndOfferedCourse_OfferedCourseIdAndIsWithdrawn
                (offeredCourse.getOfferedCourseId(), familyMember.getFamilyMemberId(), false))
            throw new InvalidRequestException(ExceptionHandlerConst.ALREADY_ENROLLED);
    }

    private FamilyCourseRegistration getFamilyCourseRegistrationEntity
            (OfferedCourse offeredCourse, FamilyMember familyMember, String loggedInMemberId) {
        return FamilyCourseRegistration.builder()
                .enrollmentDate(LocalDate.now())
                .cost(getFees(offeredCourse, familyMember).getCourseFee())
                .offeredCourse(offeredCourse)
                .familyMember(familyMember)
                .enrollmentActorId(UUID.fromString(loggedInMemberId))
                .withdrawnCredits(0)
                .isWithdrawn(false)
                .build();
    }

    public void addToWaitlist(String loggedInMemberId, CourseEnrollmentWaitlistDto waitlistDto) {
        FamilyMember loggedInMember = checkUnauthorizedAccess(loggedInMemberId);
        FamilyMember familyMember = familyMemberRepository.findByMemberLoginIdAndIsActive
                (waitlistDto.getFamilyMemberLoginId(), true).orElseThrow();
        OfferedCourse offeredCourse = offeredCourseRepository.findByBarCodeAndAvailableForEnrollment
                        (UUID.fromString(waitlistDto.getOfferedCourseBarCode()), AvailableForEnrollment.WAITLIST_OPEN)
                .orElseThrow(() -> new InvalidRequestException(ExceptionHandlerConst.INVALID_OFFERED_COURSE_ID));
        if (offeredCourseWaitlistRepository.existsByFamilyMember_FamilyMemberIdAndOfferedCourse_OfferedCourseId
                (offeredCourse.getOfferedCourseId(), familyMember.getFamilyMemberId()))
            throw new InvalidRequestException(ExceptionHandlerConst.ALREADY_WAITLISTED);
        if (familyCourseRegistrationRepository.existsByFamilyMember_FamilyMemberIdAndOfferedCourse_OfferedCourseIdAndIsWithdrawn
                (offeredCourse.getOfferedCourseId(), familyMember.getFamilyMemberId(), false))
            throw new InvalidRequestException(ExceptionHandlerConst.WAITLIST_ADD_FAILED_ALREADY_ENROLLED);
        if (offeredCourse.getNoOfSpots() == offeredCourse.getOfferedCourseWaitlist().size()) {
            throw new InvalidRequestException(ExceptionHandlerConst.WAITLIST_FULL);
        }
        OfferedCourseWaitlist courseWaitlist = OfferedCourseWaitlist.builder().offeredCourse(offeredCourse)
                .familyMember(familyMember)
                .enrollmentActorId(loggedInMember.getFamilyMemberId())
                .build();
        offeredCourseWaitlistRepository.save(courseWaitlist);
        asyncJobsService.checkAndUpdateCourseAvailabilityToNotAvailable(offeredCourse);
    }

    public List<FamilyCourseRegistrationDetails> getRegisteredCourses(String loggedInMemberId) {
        FamilyMember loggedInFamilyMember = checkUnauthorizedAccess(loggedInMemberId);
        UUID loggedInMemberIdUUID = loggedInFamilyMember.getFamilyMemberId();
        List<FamilyCourseRegistration> familyCourseRegistrationList = familyCourseRegistrationRepository
                .findAllByEnrollmentActorIdOrFamilyMember_FamilyMemberId(loggedInMemberIdUUID, loggedInMemberIdUUID);
        return familyCourseRegistrationMapper.toDtoList(familyCourseRegistrationList);
    }

    public List<OfferedCourseWaitlistDto> getWaitlistedCourses(String loggedInMemberId) {
        FamilyMember loggedInFamilyMember = checkUnauthorizedAccess(loggedInMemberId);
        UUID loggedInMemberIdUUID = loggedInFamilyMember.getFamilyMemberId();
        List<OfferedCourseWaitlist> offeredCourseWaitlist = offeredCourseWaitlistRepository
                .findAllByEnrollmentActorIdOrFamilyMember_FamilyMemberId(loggedInMemberIdUUID, loggedInMemberIdUUID);
        return offeredCourseWaitlistMapper.toDtoList(offeredCourseWaitlist);
    }

    @Transactional
    public void withdrawFromCourse(String familyCourseRegistrationId, String loggedInMemberId) {
        FamilyMember loggedInFamilyMember = checkUnauthorizedAccess(loggedInMemberId);
        UUID loggedInMemberIdUUID = loggedInFamilyMember.getFamilyMemberId();
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

    private FamilyMember checkUnauthorizedAccess(String loggedInMemberId) {
        return familyMemberRepository.findByMemberLoginIdAndIsActive(loggedInMemberId, true)
                .orElseThrow(() -> new UnauthorizedAccessException(ExceptionHandlerConst.UNAUTHORIZED_ACCESS));
    }
}
