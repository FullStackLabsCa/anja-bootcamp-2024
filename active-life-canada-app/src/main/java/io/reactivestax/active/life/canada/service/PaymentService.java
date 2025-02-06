package io.reactivestax.active.life.canada.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;


@Service
//@RequiredArgsConstructor
public class PaymentService {

    @Value("${stripe.api.baseUrl}")
    private String baseUrl;

    @Value("${stripe.api.key}")
    private String apiKey;

    private final RestTemplate restTemplate = new RestTemplate();

    public void createPaymentIntent(Integer amount) {

    }

    public void createPaymentLink(Long amount, String courseName) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set("Authorization", "Bearer " + apiKey);
        httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("line_items[0][price]", "price_1QpBSKRuekHGJW01ahADSeLX");
        requestBody.put("line_items[0][quantity]", "1");
        requestBody.put("currency", "CAD");

        String requestBodyString = requestBody.entrySet()
                .stream()
                .map(entry -> entry.getKey() + "=" + entry.getValue())
                .reduce((a, b) -> a + "&" + b)
                .orElse("");

        HttpEntity<String> requestEntity = new HttpEntity<>(requestBodyString, httpHeaders);

        // Make the API call
        ResponseEntity<Map> response = restTemplate
                .exchange(baseUrl, HttpMethod.POST, requestEntity, Map.class);

        // Extract and return the payment link
        System.out.println(">>>>>>>>>>>>>" + response.getBody());
    }
}
