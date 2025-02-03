package io.reactivestax.active.life.canada.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TwoFactorLoginRequest {
    private String token;
    private String otp;
}
