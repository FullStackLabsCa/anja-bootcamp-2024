package io.reactivestax.ems.dto;

import io.reactivestax.ems.constant.ValidationMessage;
import io.reactivestax.ems.constant.ValidationRegexPattern;
import io.reactivestax.ems.validation.CallGroup;
import io.reactivestax.ems.validation.EmailGroup;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class MessageDTO {
    @NotBlank(message = ValidationMessage.EMPTY_CUSTOMER_ID)
    private String customerId;

    @NotBlank(groups = {CallGroup.class, EmailGroup.class}, message = ValidationMessage.EMPTY_PHONE_NUMBER)
    @Size(groups = {CallGroup.class, EmailGroup.class}, min = 12, max = 12, message = ValidationMessage.INVALID_PHONE_NUMBER)
    @Pattern(groups = {CallGroup.class, EmailGroup.class}, regexp = ValidationRegexPattern.PHONE_REGEX, message = ValidationMessage.INVALID_PHONE_NUMBER)
    private String phoneNumber;

    @NotBlank(groups = EmailGroup.class, message = ValidationMessage.EMPTY_EMAIL)
    @Email(groups = EmailGroup.class, message = ValidationMessage.INVALID_EMAIL)
    private String email;

    @NotBlank(message = ValidationMessage.EMPTY_MESSAGE)
    private String message;
}

