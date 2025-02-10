package io.reactivestax.active.life.canada.service;

import io.reactivestax.active.life.canada.constant.TestData;
import io.reactivestax.active.life.canada.entity.*;
import io.reactivestax.active.life.canada.repository.AccountActivationRequestRepository;
import io.reactivestax.active.life.canada.repository.FamilyGroupRepository;
import io.reactivestax.active.life.canada.repository.OfferedCourseRepository;
import io.reactivestax.active.life.canada.repository.OfferedCourseWaitlistRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
class AsyncJobsServiceTest {

    @Autowired
    private AsyncJobsService asyncJobsService;

    @MockitoBean
    private AccountActivationRequestRepository accountActivationRequestRepository;

    @MockitoBean
    private OfferedCourseRepository offeredCourseRepository;

    @MockitoBean
    private OfferedCourseWaitlistRepository offeredCourseWaitlistRepository;

    @MockitoBean
    private FamilyGroupRepository familyGroupRepository;

    @MockitoBean
    private EmsService emsService;

    @MockitoBean
    private CacheService cacheService;

    @Test
    void testCreateAccountActivationRequestEntryAndSendToEms() {
        FamilyMember familyMember = new FamilyMember();
        familyMember.setFamilyMemberId(TestData.FAMILY_MEMBER_ID_UUID);
        familyMember.setName(TestData.MEMBER_NAME);

        AccountActivationRequest request = AccountActivationRequest.builder()
                .familyMemberId(familyMember.getFamilyMemberId())
                .token(TestData.FAMILY_MEMBER_ID_UUID)
                .build();

        when(accountActivationRequestRepository.save(any(AccountActivationRequest.class))).thenReturn(request);
        asyncJobsService.createAccountActivationRequestEntryAndSendToEms(familyMember);
        verify(accountActivationRequestRepository, timeout(100)).save(any(AccountActivationRequest.class));
        verify(emsService, times(1)).sendToEms(any(FamilyMember.class), anyString());
    }

    @Test
    void testCheckAndUpdateCourseAvailabilityToWaitlist(){
        OfferedCourse offeredCourse = OfferedCourse.builder()
                .noOfSpots(2)
                .familyCourseRegistrations(List.of(FamilyCourseRegistration.builder().isWithdrawn(false).build()))
                .build();
        doReturn(new OfferedCourse()).when(offeredCourseRepository).save(offeredCourse);
        asyncJobsService.checkAndUpdateCourseAvailabilityToWaitlist(offeredCourse);
        verify(offeredCourseRepository, timeout(100)).save(any(OfferedCourse.class));
    }

    @Test
    void testCheckAndUpdateCourseAvailabilityToNotAvailable(){
        OfferedCourse offeredCourse = OfferedCourse.builder()
                .noOfSpots(2)
                .offeredCourseWaitlist(List.of(new OfferedCourseWaitlist()))
                .build();
        doReturn(new OfferedCourse()).when(offeredCourseRepository).save(offeredCourse);
        asyncJobsService.checkAndUpdateCourseAvailabilityToNotAvailable(offeredCourse);
        verify(offeredCourseRepository, timeout(100)).save(any(OfferedCourse.class));
    }

    @Test
    void testGetAllWaitlistedMembersByOfferedCourseIdAndSendToEms() {
        UUID offeredCourseId = TestData.FAMILY_MEMBER_ID_UUID;
        String courseName = TestData.COURSE_NAME;

        FamilyMember familyMember = new FamilyMember();
        familyMember.setName(TestData.MEMBER_NAME);

        OfferedCourseWaitlist waitlist = new OfferedCourseWaitlist();
        waitlist.setFamilyMember(familyMember);

        when(offeredCourseWaitlistRepository.findAllByOfferedCourse_OfferedCourseId(offeredCourseId))
                .thenReturn(List.of(waitlist));
        asyncJobsService.getAllWaitlistedMembersByOfferedCourseIdAndSendToEms(offeredCourseId, courseName);
        verify(emsService, timeout(100)).sendToEms(any(FamilyMember.class), anyString());
    }

    @Test
    void testRemoveEntryFromWaitlistIfExists() {
        UUID offeredCourseId = TestData.FAMILY_MEMBER_ID_UUID;
        UUID familyMemberId = TestData.FAMILY_MEMBER_ID_UUID;

        asyncJobsService.removeEntryFromWaitlistIfExists(offeredCourseId, familyMemberId);
        verify(offeredCourseWaitlistRepository, timeout(100))
                .deleteFromWaitlistByOfferedCourseIdAndFamilyMemberId(offeredCourseId, familyMemberId);
    }

    @Test
    void testUpdateWithDrawnCreditsInFamilyGroup() {
        UUID familyGroupId = TestData.FAMILY_MEMBER_ID_UUID;
        FamilyGroup familyGroup = new FamilyGroup();
        familyGroup.setFamilyGroupId(familyGroupId);
        familyGroup.setCredits(100.0);

        FamilyMember familyMember = new FamilyMember();
        familyMember.setFamilyGroup(familyGroup);

        OfferedCourse offeredCourse = new OfferedCourse();
        offeredCourse.setStartDate(LocalDate.now().minusDays(10));
        offeredCourse.setNoOfClassesOffered(2);

        FamilyCourseRegistration familyCourseRegistration = new FamilyCourseRegistration();
        familyCourseRegistration.setFamilyMember(familyMember);
        familyCourseRegistration.setOfferedCourse(offeredCourse);
        familyCourseRegistration.setCost(500);

        when(familyGroupRepository.save(any(FamilyGroup.class))).thenReturn(familyGroup);
        asyncJobsService.updateWithDrawnCreditsInFamilyGroup(familyCourseRegistration);
        verify(familyGroupRepository, timeout(100)).save(any(FamilyGroup.class));
        System.out.println(familyGroup.getCredits());
        assertEquals(2600.0, familyGroup.getCredits());
    }
}
