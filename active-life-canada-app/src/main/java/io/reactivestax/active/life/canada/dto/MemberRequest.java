package io.reactivestax.active.life.canada.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.reactivestax.active.life.canada.constant.ShortConstant;
import io.reactivestax.active.life.canada.enums.deserializer.GenderDeserializer;
import io.reactivestax.active.life.canada.enums.deserializer.PreferredModeOfCommunicationDeserializer;
import io.reactivestax.active.life.canada.enums.Gender;
import io.reactivestax.active.life.canada.enums.PreferredModeOfCommunication;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class MemberRequest {
    private String name;
    @JsonFormat(pattern = ShortConstant.DATE_PATTERN)
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
