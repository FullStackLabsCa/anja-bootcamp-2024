package io.reactivestax.ems.dto;

import io.reactivestax.ems.constant.ValidationMessage;
import io.reactivestax.ems.constant.ValidationRegexPattern;
import io.reactivestax.ems.validation.CallGroup;
import io.reactivestax.ems.validation.EmailGroup;
import io.reactivestax.ems.validation.MessageGroup;
import io.reactivestax.ems.validation.SmsGroup;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BaseDTO {
    @NotBlank(message = ValidationMessage.EMPTY_CUSTOMER_ID)
    private String customerId;

    @NotBlank(groups = {CallGroup.class, SmsGroup.class}, message = ValidationMessage.EMPTY_PHONE_NUMBER)
    @Pattern(groups = {CallGroup.class, SmsGroup.class}, regexp = ValidationRegexPattern.PHONE_REGEX, message =
            ValidationMessage.INVALID_PHONE_NUMBER_REGEX)
    private String phoneNumber;

    @NotBlank(groups = EmailGroup.class, message = ValidationMessage.EMPTY_EMAIL)
    @Email(groups = EmailGroup.class, message = ValidationMessage.INVALID_EMAIL)
    private String email;

    @NotBlank(groups = MessageGroup.class, message = ValidationMessage.EMPTY_MESSAGE)
    private String message;
}

