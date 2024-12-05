package io.reactivestax.type.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Position {
    private String accountNumber;
    private String securityCusip;
    private Long holding;
    private int version;
}
