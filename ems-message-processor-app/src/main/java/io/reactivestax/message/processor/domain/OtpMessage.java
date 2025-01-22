package io.reactivestax.message.processor.domain;

import io.reactivestax.message.processor.enums.OtpStatus;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class OtpMessage extends BaseMessage {
    private String otp;
    @Enumerated(value = EnumType.STRING)
    private OtpStatus otpStatus;
    private int verificationAttempt;
}
