package io.reactivestax.type.dto;

import lombok.Data;

@Data
public class PositionDTO {
    private String accountNumber;
    private String securityCusip;
    private Long holding;
    private int version;
}