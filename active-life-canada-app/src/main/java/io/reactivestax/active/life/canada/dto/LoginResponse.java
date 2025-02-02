package io.reactivestax.active.life.canada.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class LoginResponse extends SuccessfulResponse {
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private String token;
}
