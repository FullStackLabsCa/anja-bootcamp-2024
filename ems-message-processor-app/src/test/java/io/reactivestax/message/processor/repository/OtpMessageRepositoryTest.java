package io.reactivestax.message.processor.repository;

import io.reactivestax.message.processor.domain.OtpMessage;
import io.reactivestax.message.processor.enums.NotificationMethod;
import io.reactivestax.message.processor.enums.OtpStatus;
import io.reactivestax.message.processor.respository.OtpMessageRepository;
import io.reactivestax.message.processor.util.DataProvider;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
class OtpMessageRepositoryTest {

    @Autowired
    private OtpMessageRepository otpMessageRepository;

    @Test
    void testFindById() {
        OtpMessage otpMessage = OtpMessage.builder()
                .otp(DataProvider.OTP_1)
                .email(DataProvider.EMAIL)
                .customerId(DataProvider.ID_STRING)
                .notificationMethod(NotificationMethod.EMAIL)
                .otpStatus(OtpStatus.GENERATED)
                .build();
        OtpMessage saved = otpMessageRepository.save(otpMessage);
        Optional<OtpMessage> otpMessageOptional = otpMessageRepository.findById(saved.getId());
        assertThat(otpMessageOptional).isPresent();
        otpMessageOptional.ifPresent(otpMessage1 -> {
            assertThat(otpMessage1.getOtp()).isEqualTo(saved.getOtp());
            assertThat(otpMessage1.getEmail()).isEqualTo(saved.getEmail());
            assertThat(otpMessage1.getCustomerId()).isEqualTo(saved.getCustomerId());
            assertThat(otpMessage1.getNotificationMethod()).isEqualTo(saved.getNotificationMethod());
            assertThat(otpMessage1.getOtpStatus()).isEqualTo(saved.getOtpStatus());
        });
    }
}
