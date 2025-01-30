package io.reactivestax.active.life.canada.model;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class EmsVerify {
    private String customerId;
    private String otp;
}
