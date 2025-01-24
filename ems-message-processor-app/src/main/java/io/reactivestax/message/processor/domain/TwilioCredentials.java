package io.reactivestax.message.processor.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;

import java.util.UUID;

@Getter
@Entity
public class TwilioCredentials {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private String sid;
    private String authToken;
    private String messagingServiceId;
}
