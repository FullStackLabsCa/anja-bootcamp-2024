package io.reactivestax.active.life.canada.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginMemberRequest {
    private String username;
    private String password;
}
