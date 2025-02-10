package io.reactivestax.active.life.canada.service;

import io.reactivestax.active.life.canada.constant.Endpoints;
import io.reactivestax.active.life.canada.constant.ExceptionHandlerConst;
import io.reactivestax.active.life.canada.exception.InvalidRequestException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class PaymentService {

    @Value("${stripe.api.baseUrl}")
    private String baseUrl;

    private final String apiKey;

    public PaymentService(@Value("${stripe.api.key}") String apiKey) {
        this.apiKey = apiKey;
    }

    private final RestTemplate restTemplate = new RestTemplate();

    public void createPaymentIntentAndConfirm(Integer amount, String paymentMethod) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set("Authorization", "Bearer " + apiKey);
        httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("amount", amount.toString());
        requestBody.put("currency", "CAD");
        requestBody.put("confirm", "true");
        requestBody.put("automatic_payment_methods[enabled]", "true");
        requestBody.put("automatic_payment_methods[allow_redirects]", "never");
        requestBody.put("payment_method", paymentMethod);

        String requestBodyString = requestBody.entrySet()
                .stream()
                .map(entry -> entry.getKey() + "=" + entry.getValue())
                .reduce((a, b) -> a + "&" + b)
                .orElse("");

        HttpEntity<String> requestEntity = new HttpEntity<>(requestBodyString, httpHeaders);
        try {
            ResponseEntity<String> stringResponseEntity = restTemplate.postForEntity((baseUrl + Endpoints.STRIPE_PAYMENT_INTENTS), requestEntity, String.class);
            log.info(String.valueOf(stringResponseEntity));
        } catch (HttpClientErrorException ex) {
            throw new InvalidRequestException(ExceptionHandlerConst.PAYMENT_FAILED);
        }

    }
}
