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
import io.reactivestax.ems.messaging.MessageProcessor;
import io.reactivestax.ems.messaging.MessageProducer;
import io.reactivestax.ems.repository.CustomerRepository;
import io.reactivestax.ems.repository.OtpRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
public class OtpService implements MessagingService {

    private final OtpRepository otpRepository;
    private final CustomerRepository customerRepository;
    private final MessageProcessor messageProducer;
    private final EmsCommonService emsCommonService;
    private final ApplicationContext applicationContext;

    @Value("${spring.artemis.otp-queue}")
    private String queueName;

    @Value("${application.properties.otp.verification-attempt}")
    private int verificationAttempt;

    @Value("${application.properties.otp.validation-lock-period-minutes}")
    private int validationLockPeriodMin;

    @Value("${application.properties.otp.attempt-lock-period-minutes}")
    private int attemptLockPeriodMin;

    @Value("${application.properties.otp.expire-minutes}")
    private int expireMinutes;

    @Value("${application.properties.otp.try-unlock-seconds}")
    private int tryUnlockSeconds;

    @Value("${application.properties.otp.otp-attempt}")
    private int otpAttempt;

    ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(2);

    @Autowired
    public OtpService(OtpRepository otpRepository,
                      CustomerRepository customerRepository,
                      MessageProducer messageProducer,
                      EmsCommonService emsCommonService,
                      @Value("${application.properties.otp.try-unlock-seconds}")
                      int tryUnlockSeconds,
                      ApplicationContext applicationContext) {
        this.otpRepository = otpRepository;
        this.customerRepository = customerRepository;
        this.messageProducer = messageProducer;
        this.emsCommonService = emsCommonService;
        this.applicationContext = applicationContext;
        scheduledExecutorService.scheduleAtFixedRate(this::removeAttemptLock, 0, tryUnlockSeconds, TimeUnit.SECONDS);
        scheduledExecutorService.scheduleAtFixedRate(this::removeValidationLock, 0, tryUnlockSeconds, TimeUnit.SECONDS);
    }

    @Transactional
    @Override
    public void save(BaseDTO otpDTO, NotificationMethod notificationMethod) {
        Customer customer = emsCommonService.checkIfCustomerExists(otpDTO.getCustomerId());
        if (customer.getOtpLock().equals(OtpLock.NOT_LOCKED)) {
            String contact = emsCommonService.getContactValue(otpDTO.getPhoneNumber(), otpDTO.getEmail(), notificationMethod);
            if (emsCommonService.checkIfProvidedContactExistInContacts(contact, customer.getContacts())) {
                changeStatusOfAllExistingOtp(otpDTO.getCustomerId(), OtpStatus.EXPIRED);
                OtpService otpServiceBean = applicationContext.getBean(OtpService.class);
                if (otpServiceBean.checkIfAttemptLockNeeded(customer, otpDTO.getCustomerId())) {
                    throw new TooManyRequestsException(ValidationMessage.OTP_ATTEMPTS_EXCEEDED);
                }
                OtpMessage otpMessage = convertToEntity(otpDTO, notificationMethod);
                OtpMessage savedOtpMessage = otpRepository.save(otpMessage);
                messageProducer.sendMessageToQueue(queueName, savedOtpMessage.getId());
            } else throw new InvalidRequestException(emsCommonService.getValidationMessageForInvalidContact(notificationMethod));
        } else throw new TooManyRequestsException(ValidationMessage.OTP_ATTEMPTS_EXCEEDED);
    }

    @Transactional(value = Transactional.TxType.REQUIRES_NEW)
    private boolean checkIfAttemptLockNeeded(Customer customer, String customerId) {
        List<OtpMessage> allByCustomerIdAndOtpStatus = otpRepository.findNotDiscardedOtpMessageByCustomerId(customerId);
        if (allByCustomerIdAndOtpStatus.size() == otpAttempt) {
            customer.setOtpLock(OtpLock.ATTEMPT_LOCK);
            customerRepository.save(customer);
            return true;
        }
        return false;
    }

    public void verifyOtp(VerifyOtpDTO verifyOtpDTO) {
        Customer customer = emsCommonService.checkIfCustomerExists(verifyOtpDTO.getCustomerId());
        if (customer.getOtpLock() == OtpLock.VALIDATION_LOCK) {
            throw new InvalidRequestException(ValidationMessage.OTP_VERIFICATION_ATTEMPTS_EXCEEDED);
        }
        Optional<OtpMessage> otpMessageOptional = otpRepository.findAllByCustomerIdAndOtpStatus(verifyOtpDTO.getCustomerId(),
                OtpStatus.GENERATED).stream().findFirst();
        otpMessageOptional.ifPresentOrElse(otpMessage -> {
            otpMessage.setVerificationAttempt(otpMessage.getVerificationAttempt() + 1);
            if (Objects.equals(otpMessage.getOtp(), verifyOtpDTO.getOtp())) {
                if (emsCommonService.findHourDifferenceGreaterThanOrEqualToProvidedPeriod(otpMessage.getCreatedAt(),
                        expireMinutes)) {
                    otpMessage.setOtpStatus(OtpStatus.EXPIRED);
                    otpRepository.save(otpMessage);
                    throw new InvalidRequestException(ValidationMessage.OTP_EXPIRED);
                }
                changeStatusOfAllExistingOtp(customer.getCustomerId().toString(), OtpStatus.DISCARDED);
                otpMessage.setOtpStatus(OtpStatus.VERIFIED);
                otpRepository.save(otpMessage);
            } else if (otpMessage.getVerificationAttempt() == verificationAttempt) {
                customer.setOtpLock(OtpLock.VALIDATION_LOCK);
                customerRepository.save(customer);
                otpRepository.save(otpMessage);
                throw new InvalidRequestException(ValidationMessage.OTP_VERIFICATION_FAILED_WITH_ATTEMPTS_EXCEEDED);
            } else {
                otpRepository.save(otpMessage);
                throw new InvalidRequestException(ValidationMessage.INVALID_OTP);
            }
        }, () -> {
            throw new InvalidRequestException(ValidationMessage.OTP_EXPIRED_NOT_GENERATED);
        });
    }

    private void changeStatusOfAllExistingOtp(String customerId, OtpStatus otpStatus) {
        List<OtpMessage> otpMessages = otpRepository.findNotDiscardedOtpMessageByCustomerId(customerId)
                .stream().peek(otpMessage1 -> otpMessage1.setOtpStatus(otpStatus)).toList();
        otpRepository.saveAll(otpMessages);
    }

    public ValidatedOtpDTO status(String customerId) {
        emsCommonService.checkIfCustomerExists(customerId);
        Optional<OtpMessage> optionalOtpMessage = otpRepository.findAllByCustomerIdAndOtpStatus(customerId,
                OtpStatus.VERIFIED).stream().findFirst();
        return optionalOtpMessage
                .map(otpMessage ->
                        new ValidatedOtpDTO(SuccessMessage.SUCCESS_OTP_VALIDATION,
                                otpMessage.getUpdatedAt().toString()))
                .orElseThrow(() -> new InvalidRequestException(ValidationMessage.NOT_A_VALIDATED_USER));
    }

    private void removeValidationLock() {
        customerRepository.findAllWithOtpLock(OtpLock.VALIDATION_LOCK).forEach(customer -> {
            Optional<OtpMessage> otpMessageOptional = otpRepository.findAllByCustomerIdAndOtpStatus(customer.getCustomerId().toString(), OtpStatus.GENERATED).stream().findFirst();
            otpMessageOptional.ifPresent(otpMessage -> {
                if (emsCommonService.findHourDifferenceGreaterThanOrEqualToProvidedPeriod(otpMessage.getUpdatedAt(),
                        validationLockPeriodMin)) {
                    otpMessage.setOtpStatus(OtpStatus.EXPIRED);
                    otpRepository.save(otpMessage);
                    customer.setOtpLock(OtpLock.NOT_LOCKED);
                    customerRepository.save(customer);
                }
            });
        });
    }

    private void removeAttemptLock() {
        customerRepository.findAll().forEach(customer -> {
            List<OtpMessage> list = otpRepository.findNotDiscardedOtpMessageByCustomerId(customer.getCustomerId().toString())
                    .stream().filter(otpMessage ->
                            emsCommonService.findHourDifferenceGreaterThanOrEqualToProvidedPeriod(otpMessage.getUpdatedAt(),
                                    attemptLockPeriodMin))
                    .toList();
            list.forEach(otpMessage -> otpMessage.setOtpStatus(OtpStatus.DISCARDED));
            if (list.size() > 1) {
                otpRepository.saveAll(list);
                if (customer.getOtpLock().equals(OtpLock.ATTEMPT_LOCK)) {
                    customer.setOtpLock(OtpLock.NOT_LOCKED);
                    customerRepository.save(customer);
                }
            }
        });
    }

    private OtpMessage convertToEntity(BaseDTO otpDTO, NotificationMethod notificationMethod) {
        return OtpMessage.builder()
                .customerId(otpDTO.getCustomerId())
                .phone(otpDTO.getPhoneNumber())
                .email(otpDTO.getEmail())
                .otpStatus(OtpStatus.GENERATED)
                .notificationMethod(notificationMethod)
                .otp(String.valueOf(emsCommonService.generateOtp()))
                .verificationAttempt(0).build();
    }
}
