package io.reactivestax.message.processor.service;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Call;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import com.twilio.type.Twiml;
import io.reactivestax.message.processor.enums.NotificationMethod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class TwilioService {

    private final String messagingServiceId;

    @Autowired
    public TwilioService(@Value("${twilio.credentials.auth-token}")
                         String authToken,
                         @Value("${twilio.credentials.messaging-service-id}")
                         String messagingServiceId,
                         @Value("${twilio.credentials.sid}")
                         String sid) {
        this.messagingServiceId = messagingServiceId;
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
        Message.creator(
                new PhoneNumber(contact),
                messagingServiceId,
                messageToBeSent
        ).create();
    }

    public void deliverMessageViaCall(String message, String contact) {
        String response = "<Response><Say>" + message + "</Say></Response>";

        Twiml twiml = new Twiml(response);
        Call.creator(
                new PhoneNumber(contact),
                new PhoneNumber("+16473725174"),
                twiml
        ).create();
    }

    public void deliverMessageViaEmail(String message, String contact) {
        // to be implemented
    }
}
