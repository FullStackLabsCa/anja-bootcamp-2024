package io.reactivestax.active.life.canada.dto;

import io.reactivestax.active.life.canada.enums.FeeType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CourseFeeDto {
    private String feeId;
    private FeeType feeType;
    private Integer courseFee;
}
