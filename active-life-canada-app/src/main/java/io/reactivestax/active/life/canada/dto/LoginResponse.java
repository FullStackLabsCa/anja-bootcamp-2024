package io.reactivestax.active.life.canada.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LoginResponse {
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private String token;
    private String message;
}
