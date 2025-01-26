package io.reactivestax.message.processor.service;

import io.reactivestax.message.processor.domain.EnsMessage;
import io.reactivestax.message.processor.enums.NotificationMethod;
import io.reactivestax.message.processor.respository.EnsMessageRepository;
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
class EnsMessageServiceTest {

    @Autowired
    private EnsMessageService ensMessageService;

    @MockitoBean
    private EnsMessageRepository ensMessageRepository;

    @MockitoBean
    private TwilioService twilioService;

    @Test
    void testProcessEnsMessageForPhone() {
        EnsMessage ensMessage = EnsMessage.builder()
                .notificationMethod(NotificationMethod.SMS)
                .message(DataProvider.MESSAGE)
                .phone(DataProvider.CONTACT)
                .build();
        doReturn(Optional.of(ensMessage)).when(ensMessageRepository).findById(any(UUID.class));
        doNothing().when(twilioService).sendToTwilio(any(NotificationMethod.class), anyString(), anyString());
        ensMessageService.processEnsMessage(DataProvider.ID_STRING);
        verify(ensMessageRepository, atMostOnce()).findById(any(UUID.class));
        verify(twilioService, atMostOnce()).sendToTwilio(any(NotificationMethod.class), anyString(), anyString());
    }

    @Test
    void testProcessEnsMessageForEmail() {
        EnsMessage ensMessage = EnsMessage.builder()
                .notificationMethod(NotificationMethod.EMAIL)
                .message(DataProvider.MESSAGE)
                .email(DataProvider.EMAIL)
                .build();
        doReturn(Optional.of(ensMessage)).when(ensMessageRepository).findById(any(UUID.class));
        doNothing().when(twilioService).sendToTwilio(any(NotificationMethod.class), anyString(), anyString());
        ensMessageService.processEnsMessage(DataProvider.ID_STRING);
        verify(ensMessageRepository, atMostOnce()).findById(any(UUID.class));
        verify(twilioService, atMostOnce()).sendToTwilio(any(NotificationMethod.class), anyString(), anyString());
    }
}
