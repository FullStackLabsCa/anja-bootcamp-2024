package io.reactivestax.active.life.canada.service;

import io.reactivestax.active.life.canada.constant.ExceptionHandlerConst;
import io.reactivestax.active.life.canada.constant.Message;
import io.reactivestax.active.life.canada.constant.TestData;
import io.reactivestax.active.life.canada.dto.FamilyCourseRegistrationDetails;
import io.reactivestax.active.life.canada.entity.*;
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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.LocalDate;
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
    private FamilyCourseRegistrationRepository familyCourseRegistrationRepository;
    @MockitoBean
    private OfferedCourseRepository offeredCourseRepository;
    @MockitoBean
    private FamilyMemberRepository familyMemberRepository;
    @MockitoBean
    private OfferedCourseWaitlistRepository offeredCourseWaitlistRepository;
    @MockitoBean
    private FamilyCourseRegistrationMapper familyCourseRegistrationMapper;
    @MockitoBean
    private OfferedCourseWaitlistMapper offeredCourseWaitlistMapper;
    @MockitoBean
    private ActiveLifeUtil activeLifeUtil;
    @MockitoBean
    private AsyncJobsService asyncJobsService;

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

        offeredCourse = OfferedCourse.builder()
                .offeredCourseId(TestData.OFFERED_COURSE_ID_UUID)
                .availableForEnrollment(AvailableForEnrollment.AVAILABLE)
                .noOfClassesOffered(10)
                .facility(facility)
                .noOfSpots(10)
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
    void testEnrollIntoAvailableCourse() {
        when(familyMemberRepository.findByFamilyMemberIdAndIsActive(any(UUID.class), eq(true))).thenReturn(Optional.of(loggedInMember));
        when(familyMemberRepository.findByMemberLoginIdAndIsActiveAndFamilyGroup_FamilyGroupId
                (anyString(), eq(true), any(UUID.class))).thenReturn(Optional.of(familyMember));
        when(offeredCourseRepository.findByBarCode(any(UUID.class))).thenReturn(Optional.of(offeredCourse));
        when(familyCourseRegistrationRepository.existsByFamilyMember_FamilyMemberIdAndOfferedCourse_OfferedCourseIdAndIsWithdrawn
                (any(UUID.class), any(UUID.class), eq(false))).thenReturn(false);

        String result = courseRegistrationManagementService.enrollIntoOfferedCourse
                (TestData.BAR_CODE_STRING, TestData.MEMBER_LOGIN_ID, loggedInMember.getFamilyMemberId().toString());
        assertEquals(Message.ENROLLMENT_SUCCESSFUL, result);
    }

    @Test
    void testAlreadyEnrolled() {
        when(familyMemberRepository.findByFamilyMemberIdAndIsActive(any(UUID.class), eq(true))).thenReturn(Optional.of(loggedInMember));
        when(familyMemberRepository.findByMemberLoginIdAndIsActiveAndFamilyGroup_FamilyGroupId(any(), eq(true), any()))
                .thenReturn(Optional.of(familyMember));
        when(offeredCourseRepository.findByBarCode(any(UUID.class))).thenReturn(Optional.of(offeredCourse));
        when(familyCourseRegistrationRepository.existsByFamilyMember_FamilyMemberIdAndOfferedCourse_OfferedCourseIdAndIsWithdrawn(any(), any(), eq(false)))
                .thenReturn(true);

        InvalidRequestException thrown = assertThrows(InvalidRequestException.class, () ->
                courseRegistrationManagementService.enrollIntoOfferedCourse("sampleBarcode", "memberLoginId", loggedInMember.getFamilyMemberId().toString()));
        assertEquals(ExceptionHandlerConst.ALREADY_ENROLLED, thrown.getMessage());
    }

    @Test
    void testAddToWaitlist() {
        offeredCourse.setAvailableForEnrollment(AvailableForEnrollment.WAITLIST_OPEN);

        when(familyMemberRepository.findByFamilyMemberIdAndIsActive(any(UUID.class), eq(true))).thenReturn(Optional.of(loggedInMember));
        when(familyMemberRepository.findByMemberLoginIdAndIsActiveAndFamilyGroup_FamilyGroupId(any(), eq(true), any()))
                .thenReturn(Optional.of(familyMember));
        when(offeredCourseRepository.findByBarCode(any(UUID.class))).thenReturn(Optional.of(offeredCourse));
        when(familyCourseRegistrationRepository.existsByFamilyMember_FamilyMemberIdAndOfferedCourse_OfferedCourseIdAndIsWithdrawn(any(), any(), eq(false)))
                .thenReturn(false);
        when(offeredCourseWaitlistRepository.existsByFamilyMember_FamilyMemberIdAndOfferedCourse_OfferedCourseId(any(), any()))
                .thenReturn(false);

        String result = courseRegistrationManagementService.enrollIntoOfferedCourse("sampleBarcode", "memberLoginId", loggedInMember.getFamilyMemberId().toString());
        assertEquals(Message.ADDED_TO_WAITLIST, result);
    }

    @Test
    void testWithdrawFromCourse() {
        UUID courseRegistrationId = UUID.randomUUID();
        familyCourseRegistration.setFamilyCourseRegistrationId(courseRegistrationId);
        when(familyMemberRepository.existsByFamilyMemberIdAndIsActive(any(UUID.class), eq(true))).thenReturn(true);
        when(familyCourseRegistrationRepository.findByFamilyCourseRegistrationIdAndIsWithdrawnFalseAndEnrollmentActorIdOrFamilyMember_FamilyMemberId(any(UUID.class), any(), any()))
                .thenReturn(Optional.of(familyCourseRegistration));
        when(activeLifeUtil.compareDateAndTime(any(), any())).thenReturn(false);

        courseRegistrationManagementService.withdrawFromCourse(courseRegistrationId.toString(), loggedInMember.getFamilyMemberId().toString());
        assertTrue(familyCourseRegistration.getIsWithdrawn());
    }

    @Test
    void testWithdrawNotAllowedDueToTime() {
        UUID courseRegistrationId = UUID.randomUUID();
        familyCourseRegistration.setFamilyCourseRegistrationId(courseRegistrationId);
        when(familyMemberRepository.existsByFamilyMemberIdAndIsActive(any(UUID.class), eq(true))).thenReturn(true);
        when(familyCourseRegistrationRepository.findByFamilyCourseRegistrationIdAndIsWithdrawnFalseAndEnrollmentActorIdOrFamilyMember_FamilyMemberId(any(UUID.class), any(), any()))
                .thenReturn(Optional.of(familyCourseRegistration));
        when(activeLifeUtil.compareDateAndTime(any(), any())).thenReturn(true);

        InvalidRequestException thrown = assertThrows(InvalidRequestException.class, () ->
                courseRegistrationManagementService.withdrawFromCourse(courseRegistrationId.toString(), loggedInMember.getFamilyMemberId().toString()));
        assertEquals(ExceptionHandlerConst.WITHDRAW_NOT_ALLOWED, thrown.getMessage());
    }

    @Test
    void testGetRegisteredCourses() {
        when(familyMemberRepository.existsByFamilyMemberIdAndIsActive(any(UUID.class), eq(true))).thenReturn(true);
        when(familyCourseRegistrationRepository.findAllByEnrollmentActorIdOrFamilyMember_FamilyMemberId(any(), any()))
                .thenReturn(List.of(familyCourseRegistration));

        List<FamilyCourseRegistrationDetails> result = courseRegistrationManagementService.getRegisteredCourses(loggedInMember.getFamilyMemberId().toString());
        assertEquals(1, result.size());
    }

    @Test
    void testUnauthorizedAccessWhenGettingRegisteredCourses() {
        when(familyMemberRepository.existsByFamilyMemberIdAndIsActive(any(UUID.class), eq(true))).thenReturn(false);

        UnauthorizedAccessException thrown = assertThrows(UnauthorizedAccessException.class, () ->
                courseRegistrationManagementService.getRegisteredCourses(TestData.LOGGED_IN_MEMBER_ID_STRING));
        assertEquals(ExceptionHandlerConst.UNAUTHORIZED_ACCESS, thrown.getMessage());
    }
}
