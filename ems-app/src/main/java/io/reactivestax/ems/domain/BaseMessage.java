package io.reactivestax.ems.domain;

import io.reactivestax.ems.enums.NotificationMethod;
import jakarta.persistence.*;
import lombok.Data;
import lombok.experimental.SuperBuilder;

import java.util.UUID;

@Data
@SuperBuilder
@MappedSuperclass
public class BaseMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private String phone;
    private String email;
    private String customerId;
    @Enumerated(EnumType.STRING)
    private NotificationMethod notificationMethod;
}
