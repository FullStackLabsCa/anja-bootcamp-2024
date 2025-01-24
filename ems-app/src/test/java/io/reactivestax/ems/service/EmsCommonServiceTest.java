package io.reactivestax.ems.service;

import io.reactivestax.ems.constant.ValidationMessage;
import io.reactivestax.ems.domain.Contact;
import io.reactivestax.ems.domain.Customer;
import io.reactivestax.ems.enums.ContactType;
import io.reactivestax.ems.enums.NotificationMethod;
import io.reactivestax.ems.enums.OtpLock;
import io.reactivestax.ems.exception.InvalidRequestException;
import io.reactivestax.ems.repository.CustomerRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
class EmsCommonServiceTest {

    @Autowired
    private EmsCommonService emsCommonService;

    @MockitoBean
    private CustomerRepository customerRepository;

    @Test
    void testCheckIfCustomerExists() {
        String uuid = "b87ce6bd-ec3c-4d51-9c48-b68ef99301ee";
        Customer customer = Customer.builder()
                .customerId(UUID.fromString(uuid))
                .firstName("FirstName")
                .lastName("LastName")
                .otpLock(OtpLock.NOT_LOCKED)
                .build();

        when(customerRepository.findById(any(UUID.class))).thenReturn(Optional.of(customer));

        Customer customer1 = emsCommonService.checkIfCustomerExists(uuid);
        assertThat(customer1).isSameAs(customer);
    }

    @Test
    void testCheckIfProvidedContactExistInContacts() {
        List<Contact> contacts = new ArrayList<>();
        contacts.add(Contact.builder()
                .contactType(ContactType.PHONE)
                .contactValue("+12223334444")
                .build());

        contacts.add(Contact.builder()
                .contactType(ContactType.EMAIL)
                .contactValue("example@email.com")
                .build());

        assertThat(emsCommonService.checkIfProvidedContactExistInContacts("+12223334444", contacts)).isTrue();
        assertThat(emsCommonService.checkIfProvidedContactExistInContacts("+13434333234", contacts)).isFalse();
        assertThat(emsCommonService.checkIfProvidedContactExistInContacts("example@email.com", contacts)).isTrue();
        assertThat(emsCommonService.checkIfProvidedContactExistInContacts("non_existing@email.com", contacts)).isFalse();
    }

    @Test
    void testCheckIfCustomerExistsWithNonExistingCustomerId() {
        String uuid = "3d3d3d3d-ec3c-4d51-9c48-b68ef99301ee";
        when(customerRepository.findById(any(UUID.class))).thenReturn(Optional.empty());
        assertThatThrownBy(() -> emsCommonService.checkIfCustomerExists(uuid),
                ValidationMessage.INVALID_CUSTOMER_ID, InvalidRequestException.class);
    }

    @Test
    void testCheckIfCustomerExistsWithInvalidCustomerId() {
        String uuid = "nk3n3h3in3";
        assertThatThrownBy(() -> emsCommonService.checkIfCustomerExists(uuid),
                ValidationMessage.INVALID_CUSTOMER_ID, InvalidRequestException.class);
    }

    @Test
    void testGetContactValue() {
        String phone = "+12223334444";
        String email = "example@email.com";
        String contactValue1 = emsCommonService.getContactValue(phone, "", NotificationMethod.SMS);
        assertThat(contactValue1).isSameAs(phone);
        String contactValue2 = emsCommonService.getContactValue(phone, "", NotificationMethod.CALL);
        assertThat(contactValue2).isSameAs(phone);
        String contactValue3 = emsCommonService.getContactValue("", email, NotificationMethod.EMAIL);
        assertThat(contactValue3).isSameAs(email);
    }

    @Test
    void testGetValidationMessageForInvalidContact() {
        String validationMessageForInvalidContact1 =
                emsCommonService.getValidationMessageForInvalidContact(NotificationMethod.SMS);
        assertThat(validationMessageForInvalidContact1).isSameAs(ValidationMessage.INVALID_PHONE_NUMBER);
        String validationMessageForInvalidContact2 =
                emsCommonService.getValidationMessageForInvalidContact(NotificationMethod.CALL);
        assertThat(validationMessageForInvalidContact2).isSameAs(ValidationMessage.INVALID_PHONE_NUMBER);
        String validationMessageForInvalidContact3 =
                emsCommonService.getValidationMessageForInvalidContact(NotificationMethod.EMAIL);
        assertThat(validationMessageForInvalidContact3).isSameAs(ValidationMessage.INVALID_EMAIL);
    }

    @Test
    void testFindHourDifferenceGreaterThanOrEqualToProvidedPeriod() {
        LocalDateTime dateTime = LocalDateTime.now();
        boolean hourDifferenceGreaterThanOrEqualToProvidedPeriod1 =
                emsCommonService.findHourDifferenceGreaterThanOrEqualToProvidedPeriod(dateTime.minusMinutes(10), 10);
        assertThat(hourDifferenceGreaterThanOrEqualToProvidedPeriod1).isTrue();
        boolean hourDifferenceGreaterThanOrEqualToProvidedPeriod2 =
                emsCommonService.findHourDifferenceGreaterThanOrEqualToProvidedPeriod(dateTime.minusMinutes(10), 50);
        assertThat(hourDifferenceGreaterThanOrEqualToProvidedPeriod2).isFalse();
    }

    @Test
    void testGenerateOtp() {
        int otp = emsCommonService.generateOtp();
        assertThat(String.valueOf(otp).length()).isSameAs(6);
    }

    @Test
    void testGenerateOtpWithIncorrectLength() {
        int otp = emsCommonService.generateOtp();
        assertThat(String.valueOf(otp).length()).isNotSameAs(5);
    }
}
