package io.reactivestax.active.life.canada.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@RestController
public class RestApiClientController {

    private static final Logger log = LoggerFactory.getLogger(RestApiClientController.class);

    @Value("${spring.resource-uri:http://localhost:8080/time}")
    private String resourceUri;


    @GetMapping("/call-api")
    Map<String, String> restTemplate(@RegisteredOAuth2AuthorizedClient("okta") OAuth2AuthorizedClient authorizedClient) {
        OAuth2AccessToken accessToken = Objects.requireNonNull(authorizedClient).getAccessToken();
        log.info("Issued: {}, Expires {} ", accessToken.getIssuedAt(), accessToken.getExpiresAt());
        log.info("Scopes: {} ", accessToken.getScopes());
        log.info("Token: {} ", accessToken.getTokenValue());

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + accessToken.getTokenValue());
        HttpEntity<HttpHeaders> request = new HttpEntity<>(headers);

        // Make the actual HTTP GET request
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.exchange(resourceUri, HttpMethod.GET, request, String.class);

        Map<String, String> model = new HashMap<>();
        model.put("apiResponse", response.getBody());
        model.put("oauthGrantType", "client credentials flow");
        model.put("accessToken", authorizedClient.getAccessToken().getTokenValue());
        return model;
    }
}