package io.reactivestax.active.life.canada.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class FacilityDto extends NameDescription {
    private Long facilityId;
    private String streetNo;
    private String streetName;
    private String city;
    private String province;
    private String country;
    private String postalCode;
}
