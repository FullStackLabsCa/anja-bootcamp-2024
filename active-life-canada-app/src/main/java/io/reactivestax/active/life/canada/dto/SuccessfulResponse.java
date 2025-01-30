package io.reactivestax.active.life.canada.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SuccessfulResponse {
    private String message;
}
