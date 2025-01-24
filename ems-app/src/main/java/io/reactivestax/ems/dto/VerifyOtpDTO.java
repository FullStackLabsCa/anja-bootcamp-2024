package io.reactivestax.ems.dto;

import io.reactivestax.ems.constant.ValidationMessage;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VerifyOtpDTO {
    @NotBlank(message = ValidationMessage.EMPTY_CUSTOMER_ID)
    private String customerId;

    @NotBlank(message = ValidationMessage.EMPTY_OTP)
    @Size(min = 6, max = 6, message = ValidationMessage.INVALID_OTP)
    private String otp;
}
