package io.reactivestax.active.life.canada.dto;

import io.reactivestax.active.life.canada.constant.ExceptionHandlerConst;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginMemberRequest {
    @NotEmpty(message = ExceptionHandlerConst.EMPTY_USERNAME)
    private String username;

    @NotEmpty(message = ExceptionHandlerConst.EMPTY_PASSWORD)
    private String password;
}
