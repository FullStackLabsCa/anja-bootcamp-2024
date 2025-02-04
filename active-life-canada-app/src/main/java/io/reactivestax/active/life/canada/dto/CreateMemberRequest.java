package io.reactivestax.active.life.canada.dto;

import io.reactivestax.active.life.canada.constant.ExceptionHandlerConst;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.validator.constraints.Length;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class CreateMemberRequest extends MemberRequest {
    @Length(min = 5, message = ExceptionHandlerConst.MIN_PASSWORD)
    private String password;
}
