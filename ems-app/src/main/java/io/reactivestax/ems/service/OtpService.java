package io.reactivestax.ems.service;

import io.reactivestax.ems.constant.ValidationMessage;
import io.reactivestax.ems.domain.OtpMessage;
import io.reactivestax.ems.dto.BaseDTO;
import io.reactivestax.ems.dto.VerifyOtpDTO;
import io.reactivestax.ems.enums.NotificationMethod;
import io.reactivestax.ems.enums.OtpStatus;
import io.reactivestax.ems.exception.InvalidRequestException;
import io.reactivestax.ems.messaging.MessageProcessor;
import io.reactivestax.ems.messaging.MessageProducer;
import io.reactivestax.ems.repository.OtpRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class OtpService implements MessagingService {

    private final OtpRepository otpRepository;
    private final MessageProcessor messageProducer;
    private final EmsCommonService emsCommonService;

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

    @Autowired
    public OtpService(OtpRepository otpRepository,
                      MessageProducer messageProducer,
                      EmsCommonService emsCommonService) {
        this.otpRepository = otpRepository;
        this.messageProducer = messageProducer;
        this.emsCommonService = emsCommonService;
    }

    @Transactional
    @Override
    public void save(BaseDTO otpDTO, NotificationMethod notificationMethod) {
        changeStatusOfAllExistingOtp(otpDTO.getCustomerId(), OtpStatus.EXPIRED);
        OtpMessage otpMessage = convertToEntity(otpDTO, notificationMethod);
        OtpMessage savedOtpMessage = otpRepository.save(otpMessage);
        messageProducer.sendMessageToQueue(queueName, savedOtpMessage.getId());
    }

    public void verifyOtp(VerifyOtpDTO verifyOtpDTO) {
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
                changeStatusOfAllExistingOtp(verifyOtpDTO.getCustomerId(), OtpStatus.DISCARDED);
                otpMessage.setOtpStatus(OtpStatus.VERIFIED);
                otpRepository.save(otpMessage);
            } else if (otpMessage.getVerificationAttempt() == verificationAttempt) {
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

    @Transactional
    private void changeStatusOfAllExistingOtp(String customerId, OtpStatus otpStatus) {
        List<OtpMessage> otpMessages = otpRepository.findNotDiscardedAndNotVerifiedOtpMessageByCustomerId(customerId);
        otpMessages.forEach(otpMessage -> otpMessage.setOtpStatus(otpStatus));
        otpRepository.saveAll(otpMessages);
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
