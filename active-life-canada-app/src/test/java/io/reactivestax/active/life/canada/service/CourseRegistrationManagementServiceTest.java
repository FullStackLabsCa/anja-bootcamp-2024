package io.reactivestax.active.life.canada.service;

import io.reactivestax.active.life.canada.constant.ExceptionHandlerConst;
import io.reactivestax.active.life.canada.constant.TestData;
import io.reactivestax.active.life.canada.dto.CartResponse;
import io.reactivestax.active.life.canada.dto.CourseEnrollmentWaitlistDto;
import io.reactivestax.active.life.canada.dto.FamilyCourseRegistrationDetails;
import io.reactivestax.active.life.canada.dto.OfferedCourseWaitlistDto;
import io.reactivestax.active.life.canada.entity.*;
import io.reactivestax.active.life.canada.enums.AvailableForEnrollment;
import io.reactivestax.active.life.canada.enums.FeeType;
import io.reactivestax.active.life.canada.exception.InvalidRequestException;
import io.reactivestax.active.life.canada.exception.UnauthorizedAccessException;
import io.reactivestax.active.life.canada.repository.FamilyCourseRegistrationRepository;
import io.reactivestax.active.life.canada.repository.FamilyMemberRepository;
import io.reactivestax.active.life.canada.repository.OfferedCourseRepository;
import io.reactivestax.active.life.canada.repository.OfferedCourseWaitlistRepository;
import io.reactivestax.active.life.canada.util.ActiveLifeUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class CourseRegistrationManagementServiceTest {

    @Autowired
    private CourseRegistrationManagementService courseRegistrationManagementService;

    @MockitoBean
    private ActiveLifeUtil activeLifeUtil;

    @MockitoBean
    private FamilyCourseRegistrationRepository familyCourseRegistrationRepository;
    @MockitoBean
    private OfferedCourseRepository offeredCourseRepository;
    @MockitoBean
    private FamilyMemberRepository familyMemberRepository;
    @MockitoBean
    private OfferedCourseWaitlistRepository offeredCourseWaitlistRepository;
    @MockitoBean
    private AsyncJobsService asyncJobsService;
    @MockitoBean
    private CacheService cacheService;

    private FamilyMember loggedInMember;
    private FamilyMember familyMember;
    private OfferedCourse offeredCourse;
    private FamilyCourseRegistration familyCourseRegistration;

    @BeforeEach
    void setUp() {
        FamilyGroup familyGroup = FamilyGroup.builder()
                .familyGroupId(TestData.FAMILY_GROUP_ID_UUID)
                .build();

        loggedInMember = FamilyMember.builder()
                .familyMemberId(TestData.LOGGED_IN_MEMBER_ID_UUID)
                .name(TestData.LOGGED_IN_MEMBER_NAME)
                .familyGroup(familyGroup)
                .city(TestData.CITY1)
                .isActive(true)
                .isGroupAdmin(true)
                .build();

        familyMember = FamilyMember.builder()
                .familyMemberId(TestData.FAMILY_MEMBER_ID_UUID)
                .name(TestData.MEMBER_NAME)
                .city(TestData.CITY1)
                .isActive(true)
                .isGroupAdmin(false)
                .build();

        Facility facility = Facility.builder()
                .city(TestData.CITY1)
                .build();

        OfferedCourseFee offeredCourseFeeResident = OfferedCourseFee.builder()
                .feeId(TestData.UUID_ID)
                .feeType(FeeType.RESIDENT)
                .courseFee(180)
                .build();

        OfferedCourseFee offeredCourseFeeNonResident = OfferedCourseFee.builder()
                .feeId(TestData.UUID_ID)
                .feeType(FeeType.NON_RESIDENT)
                .courseFee(180)
                .build();

        Course course = Course.builder()
                .name(TestData.COURSE_NAME)
                .build();

        offeredCourse = OfferedCourse.builder()
                .offeredCourseId(TestData.OFFERED_COURSE_ID_UUID)
                .barCode(TestData.BAR_CODE_UUID)
                .availableForEnrollment(AvailableForEnrollment.AVAILABLE)
                .noOfClassesOffered(10)
                .facility(facility)
                .noOfSpots(10)
                .familyCourseRegistrations(new ArrayList<>())
                .course(course)
                .offeredCourseFees(List.of(offeredCourseFeeResident, offeredCourseFeeNonResident))
                .offeredCourseWaitlist(new ArrayList<>())
                .build();

        familyCourseRegistration = FamilyCourseRegistration.builder()
                .familyMember(familyMember)
                .offeredCourse(offeredCourse)
                .enrollmentDate(LocalDate.now())
                .cost(100)
                .isWithdrawn(false)
                .withdrawnCredits(0)
                .build();
    }

    @Test
    void addToCart_Success() {
        CourseEnrollmentWaitlistDto cartDto = CourseEnrollmentWaitlistDto.builder()
                .familyMemberLoginId(TestData.MEMBER_LOGIN_ID)
                .offeredCourseBarCode(TestData.BAR_CODE_STRING).build();

        when(familyMemberRepository.findByFamilyMemberIdAndIsActive(any(UUID.class), anyBoolean())).thenReturn(Optional.of(loggedInMember));
        when(familyMemberRepository.findByMemberLoginIdAndIsActiveAndFamilyGroup_FamilyGroupId
                (anyString(), anyBoolean(), any(UUID.class))).thenReturn(Optional.of(familyMember));
        when(offeredCourseRepository.findByBarCode(any(UUID.class))).thenReturn(Optional.of(offeredCourse));
        when(familyCourseRegistrationRepository.existsByFamilyMember_FamilyMemberIdAndOfferedCourse_OfferedCourseIdAndIsWithdrawn
                (any(UUID.class), any(UUID.class), anyBoolean())).thenReturn(false);

        assertDoesNotThrow(() -> courseRegistrationManagementService.addToCart(cartDto, TestData.LOGGED_IN_MEMBER_ID_STRING));
    }

    @Test
    void addToCart_Failed_WaitlistOpen() {
        offeredCourse.setAvailableForEnrollment(AvailableForEnrollment.WAITLIST_OPEN);
        CourseEnrollmentWaitlistDto cartDto = CourseEnrollmentWaitlistDto.builder()
                .familyMemberLoginId(TestData.MEMBER_LOGIN_ID)
                .offeredCourseBarCode(TestData.BAR_CODE_STRING).build();

        when(familyMemberRepository.findByFamilyMemberIdAndIsActive(any(UUID.class), anyBoolean())).thenReturn(Optional.of(loggedInMember));
        when(familyMemberRepository.findByMemberLoginIdAndIsActiveAndFamilyGroup_FamilyGroupId
                (anyString(), anyBoolean(), any(UUID.class))).thenReturn(Optional.of(familyMember));
        when(offeredCourseRepository.findByBarCode(any(UUID.class))).thenReturn(Optional.of(offeredCourse));
        when(familyCourseRegistrationRepository.existsByFamilyMember_FamilyMemberIdAndOfferedCourse_OfferedCourseIdAndIsWithdrawn
                (any(UUID.class), any(UUID.class), anyBoolean())).thenReturn(false);

        assertThrows(InvalidRequestException.class, () -> courseRegistrationManagementService.addToCart(cartDto,
                TestData.LOGGED_IN_MEMBER_ID_STRING));
    }

    @Test
    void addToCart_Failed_NotAvailable() {
        offeredCourse.setAvailableForEnrollment(AvailableForEnrollment.NOT_AVAILABLE);
        CourseEnrollmentWaitlistDto cartDto = CourseEnrollmentWaitlistDto.builder()
                .familyMemberLoginId(TestData.MEMBER_LOGIN_ID)
                .offeredCourseBarCode(TestData.BAR_CODE_STRING).build();

        when(familyMemberRepository.findByFamilyMemberIdAndIsActive(any(UUID.class), anyBoolean())).thenReturn(Optional.of(loggedInMember));
        when(familyMemberRepository.findByMemberLoginIdAndIsActiveAndFamilyGroup_FamilyGroupId
                (anyString(), anyBoolean(), any(UUID.class))).thenReturn(Optional.of(familyMember));
        when(offeredCourseRepository.findByBarCode(any(UUID.class))).thenReturn(Optional.of(offeredCourse));
        when(familyCourseRegistrationRepository.existsByFamilyMember_FamilyMemberIdAndOfferedCourse_OfferedCourseIdAndIsWithdrawn
                (any(UUID.class), any(UUID.class), anyBoolean())).thenReturn(false);

        assertThrows(InvalidRequestException.class, () -> courseRegistrationManagementService.addToCart(cartDto,
                TestData.LOGGED_IN_MEMBER_ID_STRING));
    }

    @Test
    void addToCart_Fail_InvalidMemberId() {
        CourseEnrollmentWaitlistDto cartDto = CourseEnrollmentWaitlistDto.builder()
                .familyMemberLoginId(TestData.MEMBER_LOGIN_ID)
                .offeredCourseBarCode(TestData.BAR_CODE_STRING).build();

        when(familyMemberRepository.findByFamilyMemberIdAndIsActive(any(UUID.class), anyBoolean())).thenReturn(Optional.of(loggedInMember));
        when(familyMemberRepository.findByMemberLoginIdAndIsActiveAndFamilyGroup_FamilyGroupId
                (anyString(), anyBoolean(), any(UUID.class))).thenReturn(Optional.empty());

        assertThrows(InvalidRequestException.class, () -> courseRegistrationManagementService.addToCart(cartDto,
                TestData.LOGGED_IN_MEMBER_ID_STRING));
    }

    @Test
    void addToCart_Fail_InvalidCourseId() {
        CourseEnrollmentWaitlistDto cartDto = CourseEnrollmentWaitlistDto.builder()
                .familyMemberLoginId(TestData.MEMBER_LOGIN_ID)
                .offeredCourseBarCode(TestData.BAR_CODE_STRING).build();

        when(familyMemberRepository.findByFamilyMemberIdAndIsActive(any(UUID.class), anyBoolean())).thenReturn(Optional.of(loggedInMember));
        when(familyMemberRepository.findByMemberLoginIdAndIsActiveAndFamilyGroup_FamilyGroupId
                (anyString(), anyBoolean(), any(UUID.class))).thenReturn(Optional.of(familyMember));
        when(offeredCourseRepository.findByBarCode(any(UUID.class))).thenReturn(Optional.empty());

        assertThrows(InvalidRequestException.class, () -> courseRegistrationManagementService.addToCart(cartDto,
                TestData.LOGGED_IN_MEMBER_ID_STRING));
    }

    @Test
    void addToCart_Fail_AlreadyEnrolled() {
        CourseEnrollmentWaitlistDto cartDto = CourseEnrollmentWaitlistDto.builder()
                .familyMemberLoginId(TestData.MEMBER_LOGIN_ID)
                .offeredCourseBarCode(TestData.BAR_CODE_STRING).build();

        when(familyMemberRepository.findByFamilyMemberIdAndIsActive(any(UUID.class), anyBoolean())).thenReturn(Optional.of(loggedInMember));
        when(familyMemberRepository.findByMemberLoginIdAndIsActiveAndFamilyGroup_FamilyGroupId
                (anyString(), anyBoolean(), any(UUID.class))).thenReturn(Optional.of(familyMember));
        when(offeredCourseRepository.findByBarCode(any(UUID.class))).thenReturn(Optional.of(offeredCourse));
        when(familyCourseRegistrationRepository.existsByFamilyMember_FamilyMemberIdAndOfferedCourse_OfferedCourseIdAndIsWithdrawn
                (any(UUID.class), any(UUID.class), anyBoolean())).thenReturn(true);

        assertThrows(InvalidRequestException.class, () -> courseRegistrationManagementService.addToCart(cartDto,
                TestData.LOGGED_IN_MEMBER_ID_STRING));
    }

    @Test
    void testGetCart_Success(){
        when(familyMemberRepository.findByFamilyMemberIdAndIsActive(any(UUID.class), anyBoolean())).thenReturn(Optional.of(loggedInMember));
        when(cacheService.getCart(anyString())).thenReturn(List.of(CourseEnrollmentWaitlistDto.builder()
                .familyMemberLoginId(TestData.MEMBER_LOGIN_ID)
                .offeredCourseBarCode(TestData.BAR_CODE_STRING).build()));
        when(offeredCourseRepository.findByBarCode(any(UUID.class))).thenReturn(Optional.of(offeredCourse));
        when(familyMemberRepository.findByMemberLoginId(anyString())).thenReturn(Optional.of(familyMember));

        List<CartResponse> cart = courseRegistrationManagementService.getCart(TestData.LOGGED_IN_MEMBER_ID_STRING);

        assertEquals(cart.size(), 1);
    }

    @Test
    void testGetCartNonResidentFees_Success(){
        familyMember.setCity(TestData.CITY2);
        when(familyMemberRepository.findByFamilyMemberIdAndIsActive(any(UUID.class), anyBoolean())).thenReturn(Optional.of(loggedInMember));
        when(cacheService.getCart(anyString())).thenReturn(List.of(CourseEnrollmentWaitlistDto.builder()
                .familyMemberLoginId(TestData.MEMBER_LOGIN_ID)
                .offeredCourseBarCode(TestData.BAR_CODE_STRING).build()));
        when(offeredCourseRepository.findByBarCode(any(UUID.class))).thenReturn(Optional.of(offeredCourse));
        when(familyMemberRepository.findByMemberLoginId(anyString())).thenReturn(Optional.of(familyMember));

        List<CartResponse> cart = courseRegistrationManagementService.getCart(TestData.LOGGED_IN_MEMBER_ID_STRING);

        assertEquals(cart.size(), 1);
    }

//    @Test
//    void testGetFees(){
//        courseRegistrationManagementService.
//    }

    @Test
    void testWithdrawFromCourse() {
        familyCourseRegistration.setFamilyCourseRegistrationId(TestData.UUID_ID);
        when(familyMemberRepository.findByFamilyMemberIdAndIsActive(any(UUID.class), anyBoolean())).thenReturn(Optional.of(loggedInMember));
        when(familyCourseRegistrationRepository.findByFamilyCourseRegistrationIdAndIsWithdrawnFalseAndEnrollmentActorIdOrFamilyMember_FamilyMemberId
                (any(UUID.class), any(UUID.class), any(UUID.class))).thenReturn(Optional.of(familyCourseRegistration));
        when(activeLifeUtil.compareDateAndTime(any(LocalDate.class), any(LocalTime.class))).thenReturn(false);

        courseRegistrationManagementService.withdrawFromCourse(TestData.STRING_ID, loggedInMember.getFamilyMemberId().toString());

        assertTrue(familyCourseRegistration.getIsWithdrawn());
    }

    @Test
    void testWithdrawFromCourse_ForNonExistingOrInactiveMember_ThrowsException() {
        when(familyMemberRepository.existsByFamilyMemberIdAndIsActive(any(UUID.class), eq(true))).thenReturn(false);

        UnauthorizedAccessException unauthorizedAccessException = assertThrows(UnauthorizedAccessException.class, () -> courseRegistrationManagementService
                .withdrawFromCourse(TestData.STRING_ID, TestData.LOGGED_IN_MEMBER_ID_STRING));

        assertEquals(ExceptionHandlerConst.UNAUTHORIZED_ACCESS, unauthorizedAccessException.getMessage());
    }

    @Test
    void testWithdrawNotAllowedDueToTime() {
        UUID courseRegistrationId = UUID.randomUUID();
        String courseRegistrationIdString = courseRegistrationId.toString();
        familyCourseRegistration.setFamilyCourseRegistrationId(courseRegistrationId);
        when(familyMemberRepository.findByFamilyMemberIdAndIsActive(any(UUID.class), anyBoolean())).thenReturn(Optional.of(loggedInMember));
        when(familyCourseRegistrationRepository.findByFamilyCourseRegistrationIdAndIsWithdrawnFalseAndEnrollmentActorIdOrFamilyMember_FamilyMemberId(any(UUID.class), any(), any()))
                .thenReturn(Optional.of(familyCourseRegistration));
        when(activeLifeUtil.compareDateAndTime(any(), any())).thenReturn(true);

        InvalidRequestException thrown = assertThrows(InvalidRequestException.class, () ->
                courseRegistrationManagementService.withdrawFromCourse(courseRegistrationIdString, TestData.LOGGED_IN_MEMBER_ID_STRING));
        assertEquals(ExceptionHandlerConst.WITHDRAW_NOT_ALLOWED, thrown.getMessage());
    }

    @Test
    void testGetRegisteredCourses() {
        when(familyMemberRepository.findByFamilyMemberIdAndIsActive(any(UUID.class), anyBoolean())).thenReturn(Optional.of(loggedInMember));
        when(familyCourseRegistrationRepository.findAllByEnrollmentActorIdOrFamilyMember_FamilyMemberId(any(UUID.class), any(UUID.class)))
                .thenReturn(List.of(familyCourseRegistration));

        List<FamilyCourseRegistrationDetails> result = courseRegistrationManagementService.getRegisteredCourses
                (loggedInMember.getFamilyMemberId().toString());

        assertEquals(1, result.size());
    }

    @Test
    void testUnauthorizedAccessWhenGettingRegisteredCourses() {
        when(familyMemberRepository.existsByFamilyMemberIdAndIsActive(any(UUID.class), eq(true))).thenReturn(false);

        UnauthorizedAccessException thrown = assertThrows(UnauthorizedAccessException.class, () ->
                courseRegistrationManagementService.getRegisteredCourses(TestData.LOGGED_IN_MEMBER_ID_STRING));
        assertEquals(ExceptionHandlerConst.UNAUTHORIZED_ACCESS, thrown.getMessage());
    }

    @Test
    void testGetWaitlistedCourses() {
        when(familyMemberRepository.findByFamilyMemberIdAndIsActive(any(UUID.class), anyBoolean())).thenReturn(Optional.of(loggedInMember));
        when(offeredCourseWaitlistRepository.findAllByEnrollmentActorIdOrFamilyMember_FamilyMemberId
                (any(UUID.class), any(UUID.class))).thenReturn(List.of(new OfferedCourseWaitlist()));

        List<OfferedCourseWaitlistDto> result = courseRegistrationManagementService.getWaitlistedCourses
                (loggedInMember.getFamilyMemberId().toString());

        assertEquals(1, result.size());
    }

    @Test
    void testUnauthorizedAccessWhenGettingWaitlistedCourses() {
        when(familyMemberRepository.existsByFamilyMemberIdAndIsActive(any(UUID.class), eq(true)))
                .thenReturn(false);

        UnauthorizedAccessException thrown = assertThrows(UnauthorizedAccessException.class, () ->
                courseRegistrationManagementService.getWaitlistedCourses(TestData.LOGGED_IN_MEMBER_ID_STRING));
        assertEquals(ExceptionHandlerConst.UNAUTHORIZED_ACCESS, thrown.getMessage());
    }
}
