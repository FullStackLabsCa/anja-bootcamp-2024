package io.reactivestax.active.life.canada.service;

import io.reactivestax.active.life.canada.constant.ExceptionHandlerConst;
import io.reactivestax.active.life.canada.exception.InvalidRequestException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@SpringBootTest
class PaymentServiceTest {

    @Autowired
    private PaymentService paymentService;

    @MockitoBean
    private RestTemplate restTemplate;

    @Test
    void testCreatePaymentIntentAndConfirm_Success() {
        when(restTemplate.postForEntity(anyString(), any(HttpEntity.class), eq(String.class)))
                .thenReturn(new ResponseEntity<>("", HttpStatus.OK));

        assertDoesNotThrow(() -> paymentService.createPaymentIntentAndConfirm(200, ""));
    }

    @Test
    void testCreatePaymentIntentAndConfirm_Failed() {
        when(restTemplate.postForEntity(anyString(), any(HttpEntity.class), eq(String.class)))
                .thenThrow(new HttpClientErrorException(HttpStatus.BAD_REQUEST));

        InvalidRequestException invalidRequestException = assertThrows(InvalidRequestException.class,
                () -> paymentService.createPaymentIntentAndConfirm(200, ""));
        assertEquals(ExceptionHandlerConst.PAYMENT_FAILED, invalidRequestException.getMessage());
    }
}
