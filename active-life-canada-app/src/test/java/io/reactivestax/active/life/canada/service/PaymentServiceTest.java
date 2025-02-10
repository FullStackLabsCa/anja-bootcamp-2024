package io.reactivestax.active.life.canada.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@SpringBootTest
class PaymentServiceTest {

    @Autowired
    private PaymentService paymentService;

    @MockitoBean
    private RestTemplate restTemplate;

    @Test
    void testCreatePaymentIntentAndConfirm_Success() {
        when(restTemplate.postForEntity(any(), any(HttpEntity.class), eq(String.class)))
                .thenReturn(new ResponseEntity<>("", HttpStatus.OK));

        assertDoesNotThrow(() -> paymentService.createPaymentIntentAndConfirm(200, ""));
    }

//    @Test
//    void testCreatePaymentIntentAndConfirm_Failed() {
//        when(restTemplate.postForEntity(any(), any(HttpEntity.class), eq(String.class)))
//                .thenReturn(ResponseEntity.badRequest().build());
//
//        assertThrows(InvalidRequestException.class,
//                () -> paymentService.createPaymentIntentAndConfirm(200, ""));
//    }
}
