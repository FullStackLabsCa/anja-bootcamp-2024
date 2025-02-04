package io.reactivestax.active.life.canada.dto;

import io.reactivestax.active.life.canada.constant.ExceptionHandlerConst;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TwoFactorLoginRequest {
    @NotEmpty(message = ExceptionHandlerConst.EMPTY_TOKEN)
    private String token;

    @NotEmpty(message = ExceptionHandlerConst.EMPTY_OTP)
    private String otp;
}
