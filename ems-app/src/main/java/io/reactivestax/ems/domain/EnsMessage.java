package io.reactivestax.ems.domain;

import io.reactivestax.ems.enums.MessageStatus;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.*;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@Entity
public class EnsMessage extends BaseMessage {
    private String message;

    @Enumerated(value = EnumType.STRING)
    private MessageStatus messageStatus;
}


