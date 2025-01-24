package io.reactivestax.ems.service;

import io.reactivestax.ems.constant.ValidationMessage;
import io.reactivestax.ems.domain.Contact;
import io.reactivestax.ems.domain.Customer;
import io.reactivestax.ems.enums.NotificationMethod;
import io.reactivestax.ems.exception.InvalidRequestException;
import io.reactivestax.ems.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class EmsCommonService {

    private final CustomerRepository customerRepository;

    private final Random random = new Random();

    @Autowired
    public EmsCommonService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public Customer checkIfCustomerExists(String customerId) {
        Optional<Customer> optionalCustomer = customerRepository.findById(getUUIDFromString(customerId));
        if (optionalCustomer.isEmpty()) throw new InvalidRequestException(ValidationMessage.INVALID_CUSTOMER_ID);
        else return optionalCustomer.get();
    }

    private UUID getUUIDFromString(String id) {
        try {
            return UUID.fromString(id);
        } catch (IllegalArgumentException exception) {
            throw new InvalidRequestException(ValidationMessage.INVALID_CUSTOMER_ID);
        }
    }

    public boolean checkIfProvidedContactExistInContacts(String contactValue, List<Contact> contacts) {
        Optional<Contact> contactOptional =
                contacts.stream().filter(contact ->
                        Objects.equals(contact.getContactValue(), contactValue)).findFirst();
        return contactOptional.isPresent();
    }

    public String getContact(String phone, String email, NotificationMethod notificationMethod) {
        return notificationMethod == NotificationMethod.EMAIL ? email : phone;
    }

    public String getValidationMessageForInvalidContact(NotificationMethod notificationMethod) {
        return notificationMethod == NotificationMethod.EMAIL ? ValidationMessage.INVALID_EMAIL :
                ValidationMessage.INVALID_PHONE_NUMBER;
    }

    public boolean findHourDifferenceGreaterThanOrEqualToProvidedPeriod(LocalDateTime createdDateTime, int period) {
        LocalDateTime currentDateTime = LocalDateTime.now();
        Duration duration = Duration.between(createdDateTime, currentDateTime);
        return (Math.abs(duration.toMinutes()) >= period);
    }

    public int generateOtp() {
        return 100000 + random.nextInt(900000);
    }
}
