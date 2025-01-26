package io.reactivestax.ems.service;

import io.reactivestax.ems.constant.SuccessMessage;
import io.reactivestax.ems.constant.ValidationMessage;
import io.reactivestax.ems.domain.Customer;
import io.reactivestax.ems.domain.OtpMessage;
import io.reactivestax.ems.dto.BaseDTO;
import io.reactivestax.ems.dto.ValidatedOtpDTO;
import io.reactivestax.ems.dto.VerifyOtpDTO;
import io.reactivestax.ems.enums.NotificationMethod;
import io.reactivestax.ems.enums.OtpLock;
import io.reactivestax.ems.enums.OtpStatus;
import io.reactivestax.ems.exception.InvalidRequestException;
import io.reactivestax.ems.exception.ResourceNotFoundException;
import io.reactivestax.ems.messaging.MessageProducer;
import io.reactivestax.ems.repository.CustomerRepository;
import io.reactivestax.ems.repository.OtpRepository;
import io.reactivestax.ems.util.DataProvider;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class OtpServiceTest {

    @MockitoBean
    private OtpRepository otpRepository;

    @MockitoBean
    private CustomerRepository customerRepository;

    @MockitoBean
    private EmsCommonService emsCommonService;

    @MockitoBean
    private MessageProducer messageProducer;

    @Autowired
    private OtpService otpService;

    @Test
    void testSaveOtpForUnlockedCustomerWithExistingContactAndNoAttemptLockNeeded() {
        doReturn(getCustomerEntity())
                .when(emsCommonService).checkIfCustomerExists(anyString());
        doReturn(DataProvider.CONTACT).when(emsCommonService)
                .getContactValue(anyString(), anyString(), any(NotificationMethod.class));
        doReturn(true)
                .when(emsCommonService).checkIfProvidedContactExistInContacts(anyString(), anyList());
        doReturn(Collections.singletonList(new OtpMessage()))
                .when(otpRepository).findNotDiscardedAndNotVerifiedOtpMessageByCustomerId(anyString());
        doReturn(Collections.singletonList(new OtpMessage())).when(otpRepository).saveAll(anyList());
        doReturn(false).when(emsCommonService).checkIfAttemptLockNeeded(any(Customer.class), anyString());
        doReturn(OtpMessage.builder().id(DataProvider.ID_UUID).build())
                .when(otpRepository).save(any(OtpMessage.class));
        doNothing().when(messageProducer).sendMessageToQueue(anyString(), any(UUID.class));
        otpService.save(getOtpDTO(), NotificationMethod.SMS);
    }

    @Test
    void testSaveOtpForUnlockedCustomerWithNonExistingContact() {
        BaseDTO otpDto = getOtpDTO();
        doReturn(getCustomerEntity())
                .when(emsCommonService).checkIfCustomerExists(anyString());
        doReturn(DataProvider.CONTACT).when(emsCommonService)
                .getContactValue(anyString(), anyString(), any(NotificationMethod.class));
        doReturn(false)
                .when(emsCommonService).checkIfProvidedContactExistInContacts(anyString(), anyList());
        assertThatThrownBy(() -> otpService.save(otpDto, NotificationMethod.SMS),
                ValidationMessage.INVALID_PHONE_NUMBER, InvalidRequestException.class);
    }

    @Test
    void testSaveOtpForUnlockedCustomerWithExistingContactAndAttemptLockNeeded() {
        BaseDTO otpDto = getOtpDTO();
        doReturn(getCustomerEntity())
                .when(emsCommonService).checkIfCustomerExists(anyString());
        doReturn(DataProvider.CONTACT).when(emsCommonService)
                .getContactValue(anyString(), anyString(), any(NotificationMethod.class));
        doReturn(true)
                .when(emsCommonService).checkIfProvidedContactExistInContacts(anyString(), anyList());
        doReturn(true).when(emsCommonService).checkIfAttemptLockNeeded(any(Customer.class), anyString());
        assertThatThrownBy(() -> otpService.save(otpDto, NotificationMethod.SMS),
                ValidationMessage.OTP_ATTEMPTS_EXCEEDED, InvalidRequestException.class);
    }

    @Test
    void testSaveOtpForLockedCustomer() {
        BaseDTO otpDto = getOtpDTO();
        doReturn(Customer.builder().contacts(new ArrayList<>()).otpLock(OtpLock.ATTEMPT_LOCK).build())
                .when(emsCommonService).checkIfCustomerExists(anyString());
        assertThatThrownBy(() -> otpService.save(otpDto, NotificationMethod.SMS),
                ValidationMessage.OTP_ATTEMPTS_EXCEEDED, InvalidRequestException.class);
    }

    @Test
    void testVerifyOtpForExpiredOrNotGeneratedOtp() {
        VerifyOtpDTO verifyOtpDTO = getVerifyOtpDTO();
        doReturn(getCustomerEntity())
                .when(emsCommonService).checkIfCustomerExists(anyString());
        doReturn(Collections.emptyList()).when(otpRepository).findAllByCustomerIdAndOtpStatus(anyString(), any(OtpStatus.class));

        assertThatThrownBy(() -> otpService.verifyOtp(verifyOtpDTO),
                ValidationMessage.OTP_EXPIRED_NOT_GENERATED, InvalidRequestException.class);
    }


    @Test
    void testVerifyOtpForWrongOtpAndVerificationAttemptLimitNotReached() {
        VerifyOtpDTO verifyOtpDTO = getVerifyOtpDTO();
        doReturn(getCustomerEntity())
                .when(emsCommonService).checkIfCustomerExists(anyString());
        doReturn(Collections.singletonList(OtpMessage.builder().verificationAttempt(0).otp(DataProvider.OTP_2).build()))
                .when(otpRepository).findAllByCustomerIdAndOtpStatus(anyString(),
                        any(OtpStatus.class));
        doReturn(new OtpMessage()).when(otpRepository).save(any(OtpMessage.class));
        assertThatThrownBy(() -> otpService.verifyOtp(verifyOtpDTO),
                ValidationMessage.INVALID_OTP, InvalidRequestException.class);
    }

    @Test
    void testVerifyOtpForWrongOtpAndVerificationAttemptLimitReached() {
        VerifyOtpDTO verifyOtpDTO = getVerifyOtpDTO();
        doReturn(getCustomerEntity())
                .when(emsCommonService).checkIfCustomerExists(anyString());
        doReturn(Collections.singletonList(OtpMessage.builder().verificationAttempt(2).otp(DataProvider.OTP_2).build()))
                .when(otpRepository).findAllByCustomerIdAndOtpStatus(anyString(),
                        any(OtpStatus.class));
        doReturn(new Customer()).when(customerRepository).save(any(Customer.class));
        doReturn(new OtpMessage()).when(otpRepository).save(any(OtpMessage.class));
        assertThatThrownBy(() -> otpService.verifyOtp(verifyOtpDTO),
                ValidationMessage.OTP_VERIFICATION_FAILED_WITH_ATTEMPTS_EXCEEDED, InvalidRequestException.class);
    }

    @Test
    void testVerifyOtpForCorrectAndExpiredOtp() {
        VerifyOtpDTO verifyOtpDTO = getVerifyOtpDTO();
        doReturn(getCustomerEntity())
                .when(emsCommonService).checkIfCustomerExists(anyString());
        doReturn(Collections.singletonList(OtpMessage.builder().verificationAttempt(1).otp(DataProvider.OTP_1).createdAt(LocalDateTime.now()).build()))
                .when(otpRepository).findAllByCustomerIdAndOtpStatus(anyString(),
                        any(OtpStatus.class));
        doReturn(true)
                .when(emsCommonService).findHourDifferenceGreaterThanOrEqualToProvidedPeriod(any(LocalDateTime.class), anyInt());
        doReturn(new OtpMessage()).when(otpRepository).save(any(OtpMessage.class));
        assertThatThrownBy(() -> otpService.verifyOtp(verifyOtpDTO),
                ValidationMessage.OTP_EXPIRED, InvalidRequestException.class);
    }

    @Test
    void testVerifyOtpForCorrectAndNotExpiredOtp() {
        VerifyOtpDTO verifyOtpDTO = getVerifyOtpDTO();
        doReturn(Customer.builder().customerId(DataProvider.ID_UUID)
                .contacts(new ArrayList<>()).otpLock(OtpLock.NOT_LOCKED).build())
                .when(emsCommonService).checkIfCustomerExists(anyString());
        doReturn(Collections.singletonList(OtpMessage.builder().verificationAttempt(1).otp(DataProvider.OTP_1).createdAt(LocalDateTime.now()).build()))
                .when(otpRepository).findAllByCustomerIdAndOtpStatus(anyString(),
                        any(OtpStatus.class));
        doReturn(false)
                .when(emsCommonService).findHourDifferenceGreaterThanOrEqualToProvidedPeriod(any(LocalDateTime.class), anyInt());
        doReturn(Collections.singletonList(OtpMessage.builder().otpStatus(OtpStatus.VERIFIED).build()))
                .when(otpRepository).findNotDiscardedAndNotVerifiedOtpMessageByCustomerId(anyString());
        doReturn(Collections.singletonList(new OtpMessage())).when(otpRepository).saveAll(anyList());
        doReturn(new OtpMessage()).when(otpRepository).save(any(OtpMessage.class));
        doReturn(new Customer()).when(customerRepository).save(any(Customer.class));
        otpService.verifyOtp(verifyOtpDTO);
        verify(emsCommonService, atLeastOnce()).checkIfCustomerExists(anyString());
        verify(otpRepository, atLeastOnce()).findAllByCustomerIdAndOtpStatus(anyString(), any(OtpStatus.class));
        verify(emsCommonService, atLeastOnce()).findHourDifferenceGreaterThanOrEqualToProvidedPeriod(any(LocalDateTime.class), anyInt());
        verify(otpRepository, atLeastOnce()).findNotDiscardedAndNotVerifiedOtpMessageByCustomerId(anyString());
        verify(otpRepository, atLeastOnce()).saveAll(anyList());
        verify(otpRepository, atLeastOnce()).save(any(OtpMessage.class));
        verify(customerRepository, atLeastOnce()).save(any(Customer.class));
    }

    @Test
    void testVerifyOtpWithValidationLock() {
        VerifyOtpDTO verifyOtpDTO = getVerifyOtpDTO();
        doReturn(Customer.builder().contacts(new ArrayList<>()).otpLock(OtpLock.VALIDATION_LOCK).build())
                .when(emsCommonService).checkIfCustomerExists(anyString());

        assertThatThrownBy(() -> otpService.verifyOtp(verifyOtpDTO),
                ValidationMessage.OTP_VERIFICATION_ATTEMPTS_EXCEEDED, InvalidRequestException.class);
    }

    @Test
    void testStatusForVerifiedUser() {
        LocalDateTime localDateTime = LocalDateTime.now();
        ValidatedOtpDTO validatedOtpDTO = new ValidatedOtpDTO(SuccessMessage.SUCCESS_OTP_VALIDATION,
                localDateTime.toString());
        doReturn(DataProvider.ID_UUID).when(emsCommonService).getUUIDFromString(
                DataProvider.ID_STRING);
        doReturn(Optional.of(Customer.builder().customerId(DataProvider.ID_UUID).build()))
                .when(customerRepository).findById(any(UUID.class));
        doReturn(Collections.singletonList(OtpMessage.builder().updatedAt(localDateTime).build()))
                .when(otpRepository).findAllByCustomerIdAndOtpStatus(anyString(), any(OtpStatus.class));
        ValidatedOtpDTO validatedOtpDTO1 = otpService.status(DataProvider.ID_STRING);
        assertThat(validatedOtpDTO1.toString()).hasToString(validatedOtpDTO.toString());
    }

    @Test
    void testStatusForNotVerifiedUser() {
        doReturn(DataProvider.ID_UUID).when(emsCommonService).getUUIDFromString(
                DataProvider.ID_STRING);
        doReturn(Optional.of(Customer.builder().customerId(DataProvider.ID_UUID).build()))
                .when(customerRepository).findById(any(UUID.class));
        doReturn(Collections.emptyList())
                .when(otpRepository).findAllByCustomerIdAndOtpStatus(anyString(), any(OtpStatus.class));
        assertThatThrownBy(() -> otpService.status(DataProvider.ID_STRING),
                ValidationMessage.NOT_A_VALIDATED_USER, InvalidRequestException.class);
    }

    @Test
    void testStatusForNonExistingCustomer() {
        doReturn(DataProvider.ID_UUID).when(emsCommonService).getUUIDFromString(
                DataProvider.ID_STRING);
        doReturn(Optional.empty()).when(customerRepository).findById(any(UUID.class));
        assertThatThrownBy(() -> otpService.status(DataProvider.ID_STRING),
                ValidationMessage.CUSTOMER_NOT_FOUND, ResourceNotFoundException.class);
    }

    @Test
    void testRemoveValidationLockForUnlocking() {
        doReturn(Collections.singletonList(Customer.builder()
                .customerId(DataProvider.ID_UUID)
                .build())).when(customerRepository).findAllWithOtpLock(any(OtpLock.class));
        doReturn(Collections.singletonList(new OtpMessage())).when(otpRepository).findAllByCustomerIdAndOtpStatus(anyString(),
                any(OtpStatus.class));
        doReturn(true).when(emsCommonService)
                .findHourDifferenceGreaterThanOrEqualToProvidedPeriod(any(), anyInt());
        doReturn(new OtpMessage()).when(otpRepository).save(any(OtpMessage.class));
        doReturn(new Customer()).when(customerRepository).save(any(Customer.class));
        otpService.removeValidationLock();
        verify(customerRepository, atLeastOnce()).findAllWithOtpLock(any(OtpLock.class));
        verify(otpRepository, atLeastOnce()).findAllByCustomerIdAndOtpStatus(anyString(), any(OtpStatus.class));
        verify(emsCommonService, atLeastOnce())
                .findHourDifferenceGreaterThanOrEqualToProvidedPeriod(any(), anyInt());
        verify(otpRepository, atLeastOnce()).save(any(OtpMessage.class));
        verify(customerRepository, atLeastOnce()).save(any(Customer.class));
    }

    @Test
    void testRemoveValidationLockForKeepingLocked() {
        doReturn(Collections.singletonList(Customer.builder()
                .customerId(DataProvider.ID_UUID)
                .build())).when(customerRepository).findAllWithOtpLock(any(OtpLock.class));
        doReturn(Collections.singletonList(new OtpMessage())).when(otpRepository).findAllByCustomerIdAndOtpStatus(anyString(),
                any(OtpStatus.class));
        doReturn(false).when(emsCommonService)
                .findHourDifferenceGreaterThanOrEqualToProvidedPeriod(any(), anyInt());
        doReturn(new OtpMessage()).when(otpRepository).save(any(OtpMessage.class));
        doReturn(new Customer()).when(customerRepository).save(any(Customer.class));
        otpService.removeValidationLock();
        verify(customerRepository, atLeastOnce()).findAllWithOtpLock(any(OtpLock.class));
        verify(otpRepository, atLeastOnce()).findAllByCustomerIdAndOtpStatus(anyString(), any(OtpStatus.class));
        verify(emsCommonService, atLeastOnce())
                .findHourDifferenceGreaterThanOrEqualToProvidedPeriod(any(), anyInt());
        verify(otpRepository, never()).save(any(OtpMessage.class));
        verify(customerRepository, never()).save(any(Customer.class));
    }

    @Test
    void testRemoveAttemptLockForUnlocking() {
        doReturn(Collections.singletonList(Customer.builder()
                .customerId(DataProvider.ID_UUID)
                .build())).when(customerRepository).findAllWithOtpLock(any(OtpLock.class));
        doReturn(Collections.singletonList(new OtpMessage()))
                .when(otpRepository).findNotDiscardedAndNotVerifiedOtpMessageByCustomerId(anyString());
        doReturn(true).when(emsCommonService)
                .findHourDifferenceGreaterThanOrEqualToProvidedPeriod(any(), anyInt());
        doReturn(Collections.singletonList(new OtpMessage())).when(otpRepository).saveAll(any());
        doReturn(new Customer()).when(customerRepository).save(any(Customer.class));
        otpService.removeAttemptLock();
        verify(customerRepository, atLeastOnce()).findAllWithOtpLock(any(OtpLock.class));
        verify(otpRepository, atLeastOnce()).findNotDiscardedAndNotVerifiedOtpMessageByCustomerId(anyString());
        verify(emsCommonService, atLeastOnce())
                .findHourDifferenceGreaterThanOrEqualToProvidedPeriod(any(), anyInt());
        verify(otpRepository, atLeastOnce()).saveAll(any());
        verify(customerRepository, atLeastOnce()).save(any(Customer.class));
    }

    @Test
    void testRemoveAttemptLockForKeepingLocked() {
        doReturn(Collections.singletonList(Customer.builder()
                .customerId(DataProvider.ID_UUID)
                .build())).when(customerRepository).findAllWithOtpLock(any(OtpLock.class));
        doReturn(Collections.singletonList(new OtpMessage()))
                .when(otpRepository).findNotDiscardedAndNotVerifiedOtpMessageByCustomerId(anyString());
        doReturn(false).when(emsCommonService)
                .findHourDifferenceGreaterThanOrEqualToProvidedPeriod(any(), anyInt());
        doReturn(Collections.singletonList(new OtpMessage())).when(otpRepository).saveAll(any());
        doReturn(new Customer()).when(customerRepository).save(any(Customer.class));
        otpService.removeAttemptLock();
        verify(customerRepository, atLeastOnce()).findAllWithOtpLock(any(OtpLock.class));
        verify(otpRepository, atLeastOnce()).findNotDiscardedAndNotVerifiedOtpMessageByCustomerId(anyString());
        verify(emsCommonService, atLeastOnce())
                .findHourDifferenceGreaterThanOrEqualToProvidedPeriod(any(), anyInt());
        verify(otpRepository, never()).saveAll(any());
        verify(customerRepository, never()).save(any(Customer.class));
    }

    private VerifyOtpDTO getVerifyOtpDTO(){
        return new VerifyOtpDTO(DataProvider.ID_STRING, DataProvider.OTP_1);
    }
    
    private Customer getCustomerEntity(){
       return Customer.builder().contacts(new ArrayList<>()).otpLock(OtpLock.NOT_LOCKED).build();
    }

    private BaseDTO getOtpDTO() {
        return BaseDTO.builder()
                .customerId(DataProvider.ID_STRING)
                .phoneNumber(DataProvider.CONTACT)
                .email("")
                .build();
    }
}
