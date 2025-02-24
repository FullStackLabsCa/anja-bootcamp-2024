package io.reactivestax.ems.service;

import io.reactivestax.ems.constant.ValidationMessage;
import io.reactivestax.ems.enums.NotificationMethod;
import io.reactivestax.ems.exception.InvalidRequestException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Random;
import java.util.UUID;

@Service
public class EmsCommonService {

    @Value("${application.properties.otp.otp-attempt}")
    private int otpAttempt;

    private final Random random = new Random();

    public UUID getUUIDFromString(String id) {
        try {
            return UUID.fromString(id);
        } catch (IllegalArgumentException exception) {
            throw new InvalidRequestException(ValidationMessage.INVALID_CUSTOMER_ID);
        }
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

    public int generateOtp() {
        return 100000 + random.nextInt(900000);
    }
}
