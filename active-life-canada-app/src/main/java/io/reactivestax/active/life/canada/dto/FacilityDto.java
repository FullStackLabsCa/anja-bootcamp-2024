package io.reactivestax.active.life.canada.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Data
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
