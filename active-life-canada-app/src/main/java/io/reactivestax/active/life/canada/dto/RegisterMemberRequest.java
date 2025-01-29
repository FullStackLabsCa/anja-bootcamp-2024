package io.reactivestax.active.life.canada.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.reactivestax.active.life.canada.dto.deserializer.GenderDeserializer;
import io.reactivestax.active.life.canada.enums.Gender;
import io.reactivestax.active.life.canada.enums.PreferredModeOfCommunication;
import lombok.Data;

@Data
public class RegisterMemberRequest {
    private String name;
    private String dob;
    @JsonDeserialize(using = GenderDeserializer.class)
    private Gender gender;
    private String emailId;
    private String streetNo;
    private String streetName;
    private String city;
    private String province;
    private String country;
    private String homePhone;
    private String businessPhone;
    private String language;
    private String username;
    @JsonDeserialize()
    private PreferredModeOfCommunication preferredModeOfCommunication;
}
