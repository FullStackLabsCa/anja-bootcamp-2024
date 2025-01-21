package io.reactivestax.ems.domain;

import io.reactivestax.ems.enums.OtpStatus;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@Entity
public class OtpMessage extends BaseMessage {
    private String otp;
    @Enumerated(value = EnumType.STRING)
    private OtpStatus otpStatus = OtpStatus.NOT_GENERATED;
    private int attempt;
    private long generatedDateTime;
}
