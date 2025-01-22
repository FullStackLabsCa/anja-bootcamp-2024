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
import io.reactivestax.ems.exception.TooManyRequestsException;
import io.reactivestax.ems.messaging.MessageProducer;
import io.reactivestax.ems.repository.CustomerRepository;
import io.reactivestax.ems.repository.OtpRepository;
import io.reactivestax.ems.util.EmsUtil;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Stream;

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
    public OtpService(OtpRepository otpRepository, CustomerRepository customerRepository, MessageProducer messageProducer, EmsUtil emsUtil) {
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
                checkIfAttemptLockNeeded(customer, otpDTO.getCustomerId());
                otpRepository.updateOtpStatusByCustomerIdAndOtpStatus(OtpStatus.DISCARDED, otpDTO.getCustomerId());
                OtpMessage otpMessage = convertToEntity(otpDTO, notificationMethod);
                OtpMessage savedOtpMessage = otpRepository.save(otpMessage);
                messageProducer.sendMessageToQueue(queueName, savedOtpMessage.getId());
            } else throw new InvalidRequestException(emsUtil.getValidationMessageForInvalidContact(notificationMethod));
        } else tryToRemoveOtpLock(otpDTO, customer, notificationMethod);
    }

    public void verify(VerifyOtpDTO verifyOtpDTO) {
        Customer customer = emsUtil.checkIfCustomerExists(verifyOtpDTO.getCustomerId());
        if (customer.getOtpLock() == OtpLock.VALIDATION_LOCK) {
            throw new InvalidRequestException(ValidationMessage.OTP_VERIFICATION_ATTEMPTS_EXCEEDED);
        }
        Optional<OtpMessage> otpMessageOptional = otpRepository.findByCustomerIdAndOtpStatus(verifyOtpDTO.getCustomerId(), OtpStatus.GENERATED);
        otpMessageOptional.ifPresentOrElse(otpMessage -> {
            otpMessage.setVerificationAttempt(otpMessage.getVerificationAttempt() + 1);
            if (Objects.equals(otpMessage.getOtp(), verifyOtpDTO.getOtp())) {
                customer.setOtpLock(OtpLock.NOT_LOCKED);
                customerRepository.save(customer);
                otpMessage.setOtpStatus(OtpStatus.VERIFIED);
                otpRepository.save(otpMessage);
            } else {
                if (otpMessage.getVerificationAttempt() == verificationAttempt) {
                    otpMessage.setOtpStatus(OtpStatus.VALIDATION_FAILED);
                    customer.setOtpLock(OtpLock.VALIDATION_LOCK);
                    customerRepository.save(customer);
                    otpRepository.save(otpMessage);
                    throw new InvalidRequestException(ValidationMessage.OTP_VERIFICATION_FAILED_WITH_ATTEMPTS_EXCEEDED);
                } else {
                    otpRepository.save(otpMessage);
                    throw new InvalidRequestException(ValidationMessage.INVALID_OTP);
                }
            }
        }, () -> {
            throw new InvalidRequestException(ValidationMessage.OTP_EXPIRED_NOT_GENERATED);
        });
    }

    public ValidatedOtpDTO status(String customerId) {
        emsUtil.checkIfCustomerExists(customerId);
        Optional<OtpMessage> optionalOtpMessage = otpRepository.findByCustomerIdAndOtpStatus(customerId, OtpStatus.VERIFIED);
        return optionalOtpMessage.map(otpMessage -> new ValidatedOtpDTO(SuccessMessage.SUCCESS_OTP_VALIDATION, otpMessage.getUpdatedAt().toString())).orElseThrow(() -> new InvalidRequestException(ValidationMessage.NOT_A_VALIDATED_USER));
    }

    private void checkIfAttemptLockNeeded(Customer customer, String customerId) {
        List<OtpMessage> allOtpMessageByCustomerId = otpRepository.findAllByCustomerId(customerId);
        if (allOtpMessageByCustomerId.size() == otpAttempt - 1) {
            customer.setOtpLock(OtpLock.ATTEMPT_LOCK);
            customerRepository.save(customer);
        }
    }

    private void tryToRemoveOtpLock(BaseDTO otpDTO, Customer customer, NotificationMethod notificationMethod) {
        List<OtpMessage> allOtpMessageByCustomerId = otpRepository.findAllByCustomerId(otpDTO.getCustomerId());
        if (customer.getOtpLock().equals(OtpLock.VALIDATION_LOCK)) {
            Stream<OtpMessage> otpMessageStream = allOtpMessageByCustomerId.stream().filter(otpMessage -> otpMessage.getOtpStatus().equals(OtpStatus.VALIDATION_FAILED));
            otpMessageStream.findFirst().ifPresent(otpMessage -> {
                if (findHourDifference(otpMessage.getUpdatedAt(), validationLockPeriodHr)) {
                    otpMessage.setOtpStatus(OtpStatus.DISCARDED);
                    otpRepository.save(otpMessage);
                    customer.setOtpLock(OtpLock.NOT_LOCKED);
                    customerRepository.save(customer);
                    save(otpDTO, notificationMethod);
                } else throw new TooManyRequestsException(ValidationMessage.OTP_ATTEMPTS_EXCEEDED);
            });
        } else {
            List<OtpMessage> otpMessagesToBeRemoved = allOtpMessageByCustomerId.stream().filter(otpMessage -> findHourDifference(otpMessage.getCreatedAt(), attemptLockPeriodHr)).toList();
            if (!otpMessagesToBeRemoved.isEmpty()) {
                customer.setOtpLock(OtpLock.NOT_LOCKED);
                customerRepository.save(customer);
                otpRepository.deleteAll(otpMessagesToBeRemoved);
                save(otpDTO, notificationMethod);
            } else throw new TooManyRequestsException(ValidationMessage.OTP_ATTEMPTS_EXCEEDED);
        }
    }

    private boolean findHourDifference(LocalDateTime createdDateTime, int period) {
        LocalDateTime currentDateTime = LocalDateTime.now();
        Duration duration = Duration.between(createdDateTime, currentDateTime);
        return (Math.abs(duration.toMinutes()) >= 2);
    }

    private int generateOtp() {
        return 100000 + random.nextInt(900000);
    }

    private OtpMessage convertToEntity(BaseDTO otpDTO, NotificationMethod notificationMethod) {
        return OtpMessage.builder().customerId(otpDTO.getCustomerId()).phone(otpDTO.getPhoneNumber()).email(otpDTO.getEmail()).otpStatus(OtpStatus.GENERATED).notificationMethod(notificationMethod).otp(String.valueOf(generateOtp())).verificationAttempt(0).build();
    }
}
