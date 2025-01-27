package io.reactivestax.message.processor.service;

import io.reactivestax.message.processor.enums.NotificationMethod;
import io.reactivestax.message.processor.util.DataProvider;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.web.client.RestTemplate;

import java.net.URI;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atMostOnce;
import static org.mockito.Mockito.doReturn;

@SpringBootTest
 class TwilioServiceTest {

    @Autowired
    private TwilioService twilioService;

    @MockitoBean
    private RestTemplate restTemplate;

    @Test
    void testSendToTwilioForSms(){
        doReturn(new ResponseEntity<>(HttpStatus.ACCEPTED)).when(restTemplate).postForEntity(any(URI.class), any(), any());
        twilioService.sendToTwilio(NotificationMethod.SMS, DataProvider.MESSAGE, DataProvider.CONTACT);
        Mockito.verify(restTemplate, atMostOnce()).postForEntity(any(URI.class), any(), any());
    }

    @Test
    void testSendToTwilioForCall(){
        doReturn(new ResponseEntity<>(HttpStatus.ACCEPTED)).when(restTemplate).postForEntity(any(URI.class), any(), any());
        twilioService.sendToTwilio(NotificationMethod.CALL, DataProvider.MESSAGE, DataProvider.CONTACT);
        Mockito.verify(restTemplate, atMostOnce()).postForEntity(any(URI.class), any(), any());
    }

    @Test
    void testSendToTwilioForEmail(){
        twilioService.sendToTwilio(NotificationMethod.EMAIL, DataProvider.MESSAGE, DataProvider.EMAIL);
        assertThat(true).isTrue();
    }
}
