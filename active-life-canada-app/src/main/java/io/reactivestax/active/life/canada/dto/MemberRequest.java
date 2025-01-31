package io.reactivestax.active.life.canada.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.reactivestax.active.life.canada.dto.deserializer.GenderDeserializer;
import io.reactivestax.active.life.canada.dto.deserializer.PreferredModeOfCommunicationDeserializer;
import io.reactivestax.active.life.canada.enums.Gender;
import io.reactivestax.active.life.canada.enums.PreferredModeOfCommunication;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MemberRequest {
    private String name;
    @JsonFormat(pattern = "MM/dd/yyyy")
    private LocalDate dob;
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
    @JsonDeserialize(using = PreferredModeOfCommunicationDeserializer.class)
    private PreferredModeOfCommunication preferredModeOfCommunication;
}
