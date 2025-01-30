package io.reactivestax.active.life.canada.dto;

import lombok.Data;

@Data
public class LoginMemberRequest {
    private String memberLoginId;
    private String groupPin;
}
