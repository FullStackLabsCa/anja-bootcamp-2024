package io.reactivestax.message.processor.service;

import io.reactivestax.message.processor.domain.OtpMessage;
import io.reactivestax.message.processor.enums.NotificationMethod;
import io.reactivestax.message.processor.respository.OtpMessageRepository;
import io.reactivestax.message.processor.util.DataProvider;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@SpringBootTest
class OtpMessageServiceTest {

    @Autowired
    private OtpMessageService otpMessageService;

    @MockitoBean
    private OtpMessageRepository otpMessageRepository;

    @MockitoBean
    private TwilioService twilioService;

    @Test
    void testProcessOtpMessageForPhone() {
        OtpMessage otpMessage = OtpMessage.builder()
                .notificationMethod(NotificationMethod.SMS)
                .otp(DataProvider.OTP_1)
                .phone(DataProvider.CONTACT)
                .build();
        doReturn(Optional.of(otpMessage)).when(otpMessageRepository).findById(any(UUID.class));
        doNothing().when(twilioService).sendToTwilio(any(NotificationMethod.class), anyString(), anyString());
        otpMessageService.processOtpMessage(DataProvider.ID_STRING);
        verify(otpMessageRepository, atMostOnce()).findById(any(UUID.class));
        verify(twilioService, atMostOnce()).sendToTwilio(any(NotificationMethod.class), anyString(), anyString());
    }

    @Test
    void testProcessOtpMessageForEmail() {
        OtpMessage otpMessage = OtpMessage.builder()
                .notificationMethod(NotificationMethod.EMAIL)
                .otp(DataProvider.OTP_1)
                .email(DataProvider.EMAIL)
                .build();
        doReturn(Optional.of(otpMessage)).when(otpMessageRepository).findById(any(UUID.class));
        doNothing().when(twilioService).sendToTwilio(any(NotificationMethod.class), anyString(), anyString());
        otpMessageService.processOtpMessage(DataProvider.ID_STRING);
        verify(otpMessageRepository, atMostOnce()).findById(any(UUID.class));
        verify(twilioService, atMostOnce()).sendToTwilio(any(NotificationMethod.class), anyString(), anyString());
    }
}
