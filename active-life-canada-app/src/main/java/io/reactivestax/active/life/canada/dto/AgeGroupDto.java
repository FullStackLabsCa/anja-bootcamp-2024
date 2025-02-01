package io.reactivestax.active.life.canada.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AgeGroupDto {
    private Long ageGroupId;
    private String shortCode;
    private String description;
}
