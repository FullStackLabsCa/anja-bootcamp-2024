package io.reactivestax.active.life.canada.dto;

import lombok.Data;

@Data
public class TwoFactorLoginRequest {
    private String token;
    private String otp;
}
