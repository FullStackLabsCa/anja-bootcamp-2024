package io.reactivestax.ems.service;

import io.reactivestax.ems.constant.ValidationMessage;
import io.reactivestax.ems.domain.Customer;
import io.reactivestax.ems.domain.OtpMessage;
import io.reactivestax.ems.dto.BaseDTO;
import io.reactivestax.ems.enums.NotificationMethod;
import io.reactivestax.ems.enums.OtpLock;
import io.reactivestax.ems.enums.OtpStatus;
import io.reactivestax.ems.exception.InvalidRequestException;
import io.reactivestax.ems.exception.TooManyRequestsException;
import io.reactivestax.ems.messaging.MessageProducer;
import io.reactivestax.ems.repository.CustomerRepository;
import io.reactivestax.ems.repository.OtpRepository;
import io.reactivestax.ems.util.EmsUtil;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class OtpService implements MessagingService {

    private final OtpRepository otpRepository;
    private final CustomerRepository customerRepository;
    private final MessageProducer messageProducer;
    private final EmsUtil emsUtil;
    @Value("${spring.artemis.otp-queue}")
    private String queueName;

    @Value("${application.properties.otp.verification-attempt}")
    private int verificationAttempt;

    @Value("${application.properties.otp.otp-attempt}")
    private int otpAttempt;

    @Value("${application.properties.otp.validation-lock-period-hour}")
    private int validationLockPeriodHr;

    @Value("${application.properties.otp.attempt-lock-period-hour}")
    private int attemptLockPeriodHr;

    private final Random random = new Random();

    @Autowired
    public OtpService(OtpRepository otpRepository, CustomerRepository customerRepository,
                      MessageProducer messageProducer, EmsUtil emsUtil) {
        this.otpRepository = otpRepository;
        this.customerRepository = customerRepository;
        this.messageProducer = messageProducer;
        this.emsUtil = emsUtil;
    }

    @Transactional
    @Override
    public void save(BaseDTO otpDTO, NotificationMethod notificationMethod) {
        Customer customer = emsUtil.checkIfCustomerExists(otpDTO.getCustomerId());
        if (customer.getOtpLock().equals(OtpLock.NOT_LOCKED)) {
            String contact = emsUtil.getContact(otpDTO.getPhoneNumber(), otpDTO.getEmail(), notificationMethod);
            if (emsUtil.checkIfProvidedContactExistInContacts(contact, customer.getContacts())) {

                OtpMessage otpMessage = convertToEntity(otpDTO, notificationMethod);
                OtpMessage savedOtpMessage = otpRepository.save(otpMessage);
                messageProducer.sendMessageToQueue(queueName, savedOtpMessage.getId());
            } else throw new InvalidRequestException(emsUtil.getValidationMessageForInvalidContact(notificationMethod));
        } else throw new TooManyRequestsException(ValidationMessage.OTP_ATTEMPTS_EXCEEDED);
    }


    private int generateOtp() {
        return 100000 + random.nextInt(900000);
    }

    private OtpMessage convertToEntity(BaseDTO otpDTO, NotificationMethod notificationMethod) {
        return OtpMessage.builder()
                .customerId(otpDTO.getCustomerId())
                .phone(otpDTO.getPhoneNumber())
                .email(otpDTO.getEmail())
                .otpStatus(OtpStatus.GENERATED)
                .notificationMethod(notificationMethod)
                .otp(String.valueOf(generateOtp()))
                .build();
    }
}
