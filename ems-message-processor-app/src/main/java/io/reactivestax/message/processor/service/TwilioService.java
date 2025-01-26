package io.reactivestax.message.processor.service;

import com.twilio.Twilio;
import com.twilio.twiml.VoiceResponse;
import com.twilio.twiml.voice.Say;
import io.reactivestax.message.processor.enums.NotificationMethod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Service
public class TwilioService {

    private final RestTemplate restTemplate;
    private final String sid;
    private final String authToken;

    private static final String TWILIO_BASE_URL = "https://api.twilio.com/2010-04-01/Accounts/";
    private static final String TWILIO_CONTACT = "+16473725174";

    @Autowired
    public TwilioService(@Value("${twilio.credentials.auth-token}")
                         String authToken,
                         @Value("${twilio.credentials.sid}")
                         String sid,
                         RestTemplate restTemplate) {
        this.authToken = authToken;
        this.sid = sid;
        this.restTemplate = restTemplate;
        Twilio.init(sid, authToken);
    }

    public void sendToTwilio(NotificationMethod notificationMethod, String message, String contact) {
        switch (notificationMethod) {
            case SMS -> deliverMessageViaSms(message, contact);
            case CALL -> deliverMessageViaCall(message, contact);
            case EMAIL -> deliverMessageViaEmail(message, contact);
        }
    }

    public void deliverMessageViaSms(String messageToBeSent, String contact) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setBasicAuth(sid, authToken);
        httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
        requestBody.add("From", TWILIO_CONTACT);
        requestBody.add("To", contact);
        requestBody.add("Body", messageToBeSent);
        HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity<>(requestBody, httpHeaders);
        String url = TWILIO_BASE_URL + sid + "/Messages.json";
        restTemplate.postForEntity(url, httpEntity, String.class);
    }

    public void deliverMessageViaCall(String message, String contact) {
        String url = TWILIO_BASE_URL + sid + "/Calls.json";
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setBasicAuth(sid, authToken);
        httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        String twiml = new VoiceResponse.Builder().say(new Say.Builder(message)
                        .voice(Say.Voice.POLLY_MATTHEW).build())
                .build().toXml();

        MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
        requestBody.add("From", TWILIO_CONTACT);
        requestBody.add("To", contact);
        requestBody.add("Twiml", twiml);

        HttpEntity<MultiValueMap<String, String>> callEntity = new HttpEntity<>(requestBody, httpHeaders);
        restTemplate.postForEntity(url, callEntity, String.class);
    }

    public void deliverMessageViaEmail(String message, String contact) {
        // to be implemented
    }
}
