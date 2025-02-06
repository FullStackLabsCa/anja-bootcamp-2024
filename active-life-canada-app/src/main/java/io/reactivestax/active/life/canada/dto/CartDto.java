package io.reactivestax.active.life.canada.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class CartDto {
    private String offeredCourseBarCode;
    private String familyMemberLoginId;
}
