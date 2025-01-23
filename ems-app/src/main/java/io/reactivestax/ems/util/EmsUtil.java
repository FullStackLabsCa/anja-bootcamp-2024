package io.reactivestax.ems.util;

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
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

@Component
public class EmsUtil {

    private final CustomerRepository customerRepository;
    private final OtpRepository otpRepository;

    @Value("${application.properties.otp.otp-attempt}")
    private int otpAttempt;

    private final Random random = new Random();

    @Autowired
    public EmsUtil(CustomerRepository customerRepository, OtpRepository otpRepository) {
        this.customerRepository = customerRepository;
        this.otpRepository = otpRepository;
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

    public String getContact(String phone, String email, NotificationMethod notificationMethod){
        return notificationMethod == NotificationMethod.EMAIL ? email : phone;
    }

    public String getValidationMessageForInvalidContact(NotificationMethod notificationMethod){
       return notificationMethod == NotificationMethod.EMAIL ? ValidationMessage.INVALID_EMAIL:
                ValidationMessage.INVALID_PHONE_NUMBER;
    }

    @Transactional(value = Transactional.TxType.REQUIRES_NEW)
    public boolean checkIfAttemptLockNeeded(Customer customer, String customerId) {
        List<OtpMessage> allByCustomerIdAndOtpStatus = otpRepository.findNotDiscardedOtpMessageByCustomerId(customerId);
        if (allByCustomerIdAndOtpStatus.size() == otpAttempt) {
            customer.setOtpLock(OtpLock.ATTEMPT_LOCK);
            customerRepository.save(customer);
            return true;
        }
        return false;
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
