package io.reactivestax.active.life.canada.service;

import io.reactivestax.active.life.canada.constant.TestData;
import io.reactivestax.active.life.canada.dto.CreateMemberRequest;
import io.reactivestax.active.life.canada.dto.MemberDetails;
import io.reactivestax.active.life.canada.dto.UpdateMemberRequest;
import io.reactivestax.active.life.canada.entity.FamilyGroup;
import io.reactivestax.active.life.canada.entity.FamilyMember;
import io.reactivestax.active.life.canada.exception.InvalidRequestException;
import io.reactivestax.active.life.canada.exception.UnauthorizedAccessException;
import io.reactivestax.active.life.canada.repository.FamilyGroupRepository;
import io.reactivestax.active.life.canada.repository.FamilyMemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@SpringBootTest
class FamilyManagementServiceTest {

    @Autowired
    private FamilyManagementService familyManagementService;

    @MockitoBean
    private FamilyMemberRepository familyMemberRepository;

    @MockitoBean
    private FamilyGroupRepository familyGroupRepository;

    @MockitoBean
    private AsyncJobsService asyncJobsService;

    private FamilyMember loggedInMember;
    private FamilyMember familyMember;
    private FamilyGroup familyGroup;

    @BeforeEach
    void setUp() {
        loggedInMember = FamilyMember.builder()
                .familyMemberId(TestData.FAMILY_MEMBER_ID_UUID)
                .memberLoginId(TestData.MEMBER_LOGIN_ID)
                .isActive(true)
                .isGroupAdmin(true)
                .build();
        List<FamilyMember> familyMemberList = new ArrayList<>();
        familyMemberList.add(loggedInMember);
        familyGroup = FamilyGroup.builder()
                .familyGroupId(TestData.FAMILY_GROUP_ID_UUID)
                .familyMembers(familyMemberList)
                .build();
        loggedInMember.setFamilyGroup(familyGroup);

        familyMember = FamilyMember.builder()
                .familyMemberId(TestData.FAMILY_MEMBER_ID_UUID)
                .memberLoginId(TestData.FAMILY_MEMBER_LOGIN_ID)
                .familyGroup(familyGroup)
                .build();
    }

    @Test
    void testRegisterGroupAdminFamilyMember_Success() {
        CreateMemberRequest request = CreateMemberRequest.builder()
                .username(TestData.MEMBER_LOGIN_ID)
                .password(TestData.PASSWORD)
                .build();

        when(familyMemberRepository.existsByMemberLoginId(TestData.MEMBER_LOGIN_ID)).thenReturn(false);
        when(familyGroupRepository.save(any(FamilyGroup.class))).thenReturn(familyGroup);
        doNothing().when(asyncJobsService).createAccountActivationRequestEntryAndSendToEms(any(FamilyMember.class));

        familyManagementService.createFamilyMember(request, TestData.FAMILY_MEMBER_ID_STRING, true);

        verify(familyGroupRepository, times(1)).save(any());
        verify(asyncJobsService, times(1)).createAccountActivationRequestEntryAndSendToEms(any());
    }

    @Test
    void testCreateFamilyMember_Success_GroupAdminLoggedIn() {
        CreateMemberRequest request = CreateMemberRequest.builder()
                .username(TestData.MEMBER_LOGIN_ID)
                .password(TestData.PASSWORD)
                .build();

        when(familyMemberRepository.existsByMemberLoginId(TestData.MEMBER_LOGIN_ID)).thenReturn(false);
        when(familyMemberRepository.findByFamilyMemberIdAndIsActive(any(UUID.class), anyBoolean()))
                .thenReturn(Optional.of(loggedInMember));
        when(familyGroupRepository.save(any(FamilyGroup.class))).thenReturn(familyGroup);
        doNothing().when(asyncJobsService).createAccountActivationRequestEntryAndSendToEms(any(FamilyMember.class));

        familyManagementService.createFamilyMember(request, TestData.FAMILY_MEMBER_ID_STRING, false);

        verify(familyGroupRepository, times(1)).save(any());
        verify(asyncJobsService, times(1)).createAccountActivationRequestEntryAndSendToEms(any());
    }

    @Test
    void testCreateFamilyMember_Failure_NotGroupAdminLoggedIn() {
        CreateMemberRequest request = CreateMemberRequest.builder()
                .username(TestData.MEMBER_LOGIN_ID)
                .password(TestData.PASSWORD)
                .build();
        loggedInMember.setGroupAdmin(false);

        when(familyMemberRepository.existsByMemberLoginId(TestData.MEMBER_LOGIN_ID)).thenReturn(false);
        when(familyMemberRepository.findByFamilyMemberIdAndIsActive(any(UUID.class), anyBoolean()))
                .thenReturn(Optional.of(loggedInMember));
        when(familyGroupRepository.save(any(FamilyGroup.class))).thenReturn(familyGroup);
        doNothing().when(asyncJobsService).createAccountActivationRequestEntryAndSendToEms(any(FamilyMember.class));

        assertThrows(UnauthorizedAccessException.class, () -> familyManagementService.createFamilyMember
                (request, TestData.FAMILY_MEMBER_ID_STRING, false));
    }

    @Test
    void testCreateFamilyMember_Fails_MemberAlreadyExistsForAdmin() {
        CreateMemberRequest request = CreateMemberRequest.builder()
                .username(TestData.MEMBER_LOGIN_ID)
                .password(TestData.PASSWORD)
                .build();

        when(familyMemberRepository.existsByMemberLoginId(TestData.MEMBER_LOGIN_ID)).thenReturn(true);

        assertThrows(InvalidRequestException.class, () ->
                familyManagementService.createFamilyMember(request, TestData.LOGGED_IN_MEMBER_ID_STRING, true));
    }

    @Test
    void testCreateFamilyMember_Fails_MemberAlreadyExists() {
        CreateMemberRequest request = CreateMemberRequest.builder()
                .username(TestData.MEMBER_LOGIN_ID)
                .password(TestData.PASSWORD)
                .build();

        when(familyMemberRepository.existsByMemberLoginId(TestData.MEMBER_LOGIN_ID)).thenReturn(false);
        when(familyMemberRepository.findByFamilyMemberIdAndIsActive(TestData.LOGGED_IN_MEMBER_ID_UUID, true))
                .thenReturn(Optional.empty());

        assertThrows(UnauthorizedAccessException.class, () ->
                familyManagementService.createFamilyMember(request, TestData.LOGGED_IN_MEMBER_ID_STRING, false));
    }


    @Test
    void testUpdateFamilyMember_Success_SelfUpdate() {
        UpdateMemberRequest request = new UpdateMemberRequest();

        when(familyMemberRepository.findByFamilyMemberIdAndIsActive(any(UUID.class), anyBoolean()))
                .thenReturn(Optional.of(loggedInMember));

        familyManagementService.updateFamilyMember(TestData.MEMBER_LOGIN_ID, request, TestData.LOGGED_IN_MEMBER_ID_STRING);

        verify(familyMemberRepository, times(1)).save(any());
    }

    @Test
    void testUpdateFamilyMember_Success_ChildUpdate() {
        UpdateMemberRequest request = new UpdateMemberRequest();

        when(familyMemberRepository.findByFamilyMemberIdAndIsActive(any(UUID.class), anyBoolean()))
                .thenReturn(Optional.of(loggedInMember));
        when(familyMemberRepository.findByMemberLoginIdAndIsActiveAndFamilyGroup_FamilyGroupId
                (anyString(), anyBoolean(), any(UUID.class))).thenReturn(Optional.of(familyMember));

        familyManagementService.updateFamilyMember(TestData.FAMILY_MEMBER_LOGIN_ID,
                request, TestData.LOGGED_IN_MEMBER_ID_STRING);

        verify(familyMemberRepository, times(1)).save(any());
    }

    @Test
    void testUpdateFamilyMember_Fails_InvalidMemberIdForChildUpdate() {
        UpdateMemberRequest request = new UpdateMemberRequest();

        when(familyMemberRepository.findByFamilyMemberIdAndIsActive(any(UUID.class), anyBoolean()))
                .thenReturn(Optional.of(loggedInMember));
        when(familyMemberRepository.findByMemberLoginIdAndIsActiveAndFamilyGroup_FamilyGroupId
                (anyString(), anyBoolean(), any(UUID.class))).thenReturn(Optional.empty());

        assertThrows(InvalidRequestException.class, () -> familyManagementService.updateFamilyMember
                (TestData.FAMILY_MEMBER_LOGIN_ID, request, TestData.LOGGED_IN_MEMBER_ID_STRING));
    }

    @Test
    void testUpdateFamilyMember_Fails_UnauthorizedAccess() {
        UpdateMemberRequest request = new UpdateMemberRequest();

        when(familyMemberRepository.findByFamilyMemberIdAndIsActive(any(UUID.class), anyBoolean()))
                .thenReturn(Optional.empty());

        assertThrows(UnauthorizedAccessException.class, () -> familyManagementService.updateFamilyMember
                (TestData.MEMBER_LOGIN_ID, request, TestData.LOGGED_IN_MEMBER_ID_STRING));
    }

    @Test
    void testGetFamilyMember_Success_AdminLoggedInFetchingSelf() {
        when(familyMemberRepository.findByFamilyMemberIdAndIsActive(any(UUID.class), anyBoolean()))
                .thenReturn(Optional.of(loggedInMember));

        MemberDetails result = familyManagementService
                .getFamilyMember(TestData.MEMBER_LOGIN_ID, TestData.LOGGED_IN_MEMBER_ID_STRING);

        assertNotNull(result);
    }

    @Test
    void testGetFamilyMember_Success_AdminLoggedInFetchingChild() {
        when(familyMemberRepository.findByFamilyMemberIdAndIsActive(any(UUID.class), anyBoolean()))
                .thenReturn(Optional.of(loggedInMember));
        when(familyMemberRepository.findByMemberLoginIdAndIsActiveAndFamilyGroup_FamilyGroupId
                (anyString(), anyBoolean(), any(UUID.class))).thenReturn(Optional.of(familyMember));

        MemberDetails result = familyManagementService
                .getFamilyMember(TestData.FAMILY_MEMBER_LOGIN_ID, TestData.LOGGED_IN_MEMBER_ID_STRING);

        assertNotNull(result);
    }

    @Test
    void testGetFamilyMember_Fails_AdminFetchSelfInvalidMemberId() {
        when(familyMemberRepository.findByFamilyMemberIdAndIsActive(any(UUID.class), anyBoolean()))
                .thenReturn(Optional.empty());

        assertThrows(UnauthorizedAccessException.class, () ->
                familyManagementService.getFamilyMember(TestData.MEMBER_LOGIN_ID, TestData.LOGGED_IN_MEMBER_ID_STRING));
    }

    @Test
    void testGetFamilyMember_Fails_AdminFetchingChildInvalidMemberId() {
        when(familyMemberRepository.findByFamilyMemberIdAndIsActive(any(UUID.class), anyBoolean()))
                .thenReturn(Optional.of(loggedInMember));
        when(familyMemberRepository.findByMemberLoginIdAndIsActiveAndFamilyGroup_FamilyGroupId
                (anyString(), anyBoolean(), any(UUID.class))).thenReturn(Optional.empty());

        assertThrows(InvalidRequestException.class, () ->
                familyManagementService.getFamilyMember(TestData.FAMILY_MEMBER_LOGIN_ID, TestData.LOGGED_IN_MEMBER_ID_STRING));
    }

    @Test
    void testGetFamilyMember_Fails_LoggedInMemberFetchingChild_UnauthorizedException() {
        loggedInMember.setGroupAdmin(false);
        when(familyMemberRepository.findByFamilyMemberIdAndIsActive(any(UUID.class), anyBoolean()))
                .thenReturn(Optional.of(loggedInMember));

        assertThrows(UnauthorizedAccessException.class, () ->
                familyManagementService.getFamilyMember(TestData.FAMILY_MEMBER_LOGIN_ID, TestData.LOGGED_IN_MEMBER_ID_STRING));
    }

    @Test
    void testDeactivateFamilyMember_Success_AdminLoggedInSelfDeactivate() {
        when(familyMemberRepository.findByFamilyMemberIdAndIsActive(any(UUID.class), anyBoolean()))
                .thenReturn(Optional.of(loggedInMember));

        familyManagementService.deactivateFamilyMember(TestData.MEMBER_LOGIN_ID, TestData.LOGGED_IN_MEMBER_ID_STRING);

        verify(familyMemberRepository, times(1)).updateIsActiveByFamilyMemberId(any(), eq(false));
    }

    @Test
    void testDeactivateFamilyMember_Success_AdminLoggedInChildDeactivate() {
        when(familyMemberRepository.findByFamilyMemberIdAndIsActive(any(UUID.class), anyBoolean()))
                .thenReturn(Optional.of(loggedInMember));
        when(familyMemberRepository.existsByMemberLoginIdAndIsActiveAndFamilyGroup_FamilyGroupId(anyString(), anyBoolean(), any(UUID.class)))
                .thenReturn(true);

        familyManagementService.deactivateFamilyMember(TestData.FAMILY_MEMBER_LOGIN_ID, TestData.LOGGED_IN_MEMBER_ID_STRING);

        verify(familyMemberRepository, times(1)).updateIsActiveByMemberLoginId(any(), eq(false));
    }

    @Test
    void testDeactivateFamilyMember_Fails_AdminLoggedInInvalidFamilyMemberId() {
        when(familyMemberRepository.findByFamilyMemberIdAndIsActive(any(UUID.class), anyBoolean()))
                .thenReturn(Optional.of(loggedInMember));
        when(familyMemberRepository.existsByMemberLoginIdAndIsActiveAndFamilyGroup_FamilyGroupId(anyString(), anyBoolean(), any(UUID.class)))
                .thenReturn(false);


        assertThrows(InvalidRequestException.class, () ->
                familyManagementService.deactivateFamilyMember
                        (TestData.FAMILY_MEMBER_LOGIN_ID, TestData.LOGGED_IN_MEMBER_ID_STRING));
    }

    @Test
    void testDeactivateFamilyMember_Fails_NotAdminLoggedInChildDeactivate() {
        loggedInMember.setGroupAdmin(false);
        when(familyMemberRepository.findByFamilyMemberIdAndIsActive(any(UUID.class), anyBoolean()))
                .thenReturn(Optional.of(loggedInMember));

        assertThrows(UnauthorizedAccessException.class, () ->
                familyManagementService.deactivateFamilyMember
                        (TestData.FAMILY_MEMBER_LOGIN_ID, TestData.LOGGED_IN_MEMBER_ID_STRING));
    }

    @Test
    void testDeactivateFamilyMember_Fails_UnauthorizedAccess() {
        when(familyMemberRepository.findByFamilyMemberIdAndIsActive(any(UUID.class), anyBoolean()))
                .thenReturn(Optional.empty());

        assertThrows(UnauthorizedAccessException.class, () ->
                familyManagementService.deactivateFamilyMember
                        (TestData.FAMILY_MEMBER_LOGIN_ID, TestData.LOGGED_IN_MEMBER_ID_STRING));
    }
}
