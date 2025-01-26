package io.reactivestax.ems.service;

import io.reactivestax.ems.constant.ValidationMessage;
import io.reactivestax.ems.domain.Contact;
import io.reactivestax.ems.domain.Customer;
import io.reactivestax.ems.domain.OtpMessage;
import io.reactivestax.ems.enums.NotificationMethod;
import io.reactivestax.ems.enums.OtpLock;
import io.reactivestax.ems.exception.InvalidRequestException;
import io.reactivestax.ems.repository.CustomerRepository;
import io.reactivestax.ems.repository.OtpRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class EmsCommonService {

    private final CustomerRepository customerRepository;
    private final OtpRepository otpRepository;

    @Value("${application.properties.otp.otp-attempt}")
    private int otpAttempt;

    private final Random random = new Random();

    @Autowired
    public EmsCommonService(CustomerRepository customerRepository, OtpRepository otpRepository) {
        this.customerRepository = customerRepository;
        this.otpRepository = otpRepository;
    }

    public Customer checkIfCustomerExists(String customerId) {
        Optional<Customer> optionalCustomer = customerRepository.findById(getUUIDFromString(customerId));
        if (optionalCustomer.isEmpty()) throw new InvalidRequestException(ValidationMessage.INVALID_CUSTOMER_ID);
        else return optionalCustomer.get();
    }

    public UUID getUUIDFromString(String id) {
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

    public String getContactValue(String phone, String email, NotificationMethod notificationMethod) {
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

    @Transactional(value = Transactional.TxType.REQUIRES_NEW)
    public boolean checkIfAttemptLockNeeded(Customer customer, String customerId) {
        List<OtpMessage> allByCustomerIdAndOtpStatus = otpRepository.findNotDiscardedAndNotVerifiedOtpMessageByCustomerId(customerId);
        if (allByCustomerIdAndOtpStatus.size() == otpAttempt) {
            customer.setOtpLock(OtpLock.ATTEMPT_LOCK);
            customerRepository.save(customer);
            return true;
        }
        return false;
    }

    public int generateOtp() {
        return 100000 + random.nextInt(900000);
    }
}
