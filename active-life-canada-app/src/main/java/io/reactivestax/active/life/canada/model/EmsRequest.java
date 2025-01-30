package io.reactivestax.active.life.canada.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EmsRequest {
    private String customerId;
    private String phoneNumber;
    private String email;
    private String message;
}

