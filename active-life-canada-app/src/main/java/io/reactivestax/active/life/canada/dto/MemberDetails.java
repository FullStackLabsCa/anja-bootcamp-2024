package io.reactivestax.active.life.canada.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
public class MemberDetails {
    private String username;
    private String name;
    private String dob;
    private String gender;
    private String emailId;
    private String streetNo;
    private String streetName;
    private String city;
    private String province;
    private String country;
    private String homePhone;
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private String businessPhone;
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private String language;
    private Double credits;
}
