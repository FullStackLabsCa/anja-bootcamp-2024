package io.reactivestax.active.life.canada.dto;

import io.reactivestax.active.life.canada.enums.AvailableForEnrollment;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CartResponse {
    private String courseName;
    private Integer fee;
    private AvailableForEnrollment status;
}
