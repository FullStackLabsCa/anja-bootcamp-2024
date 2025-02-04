package io.reactivestax.active.life.canada.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.reactivestax.active.life.canada.constant.ExceptionHandlerConst;
import io.reactivestax.active.life.canada.constant.ShortConstant;
import io.reactivestax.active.life.canada.constant.ValidationRegexPattern;
import io.reactivestax.active.life.canada.dto.group.CreateGroup;
import io.reactivestax.active.life.canada.enums.Gender;
import io.reactivestax.active.life.canada.enums.PreferredModeOfCommunication;
import io.reactivestax.active.life.canada.enums.deserializer.GenderDeserializer;
import io.reactivestax.active.life.canada.enums.deserializer.PreferredModeOfCommunicationDeserializer;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class MemberRequest {
    @NotEmpty(groups = CreateGroup.class, message = ExceptionHandlerConst.EMPTY_NAME)
    private String name;

    @JsonFormat(pattern = ShortConstant.DATE_PATTERN)
    private LocalDate dob;

    @JsonDeserialize(using = GenderDeserializer.class)
    private Gender gender;

    @Email(groups = CreateGroup.class, message = ExceptionHandlerConst.INVALID_EMAIL)
    @Length(groups = CreateGroup.class, min = 5, message = ExceptionHandlerConst.INVALID_EMAIL)
    private String emailId;

    @NotEmpty(groups = CreateGroup.class, message = ExceptionHandlerConst.EMPTY_STREET_NO)
    private String streetNo;

    @NotEmpty(groups = CreateGroup.class, message = ExceptionHandlerConst.EMPTY_STREET_NAME)
    private String streetName;

    @NotEmpty(groups = CreateGroup.class, message = ExceptionHandlerConst.EMPTY_CITY)
    private String city;

    @NotEmpty(groups = CreateGroup.class, message = ExceptionHandlerConst.EMPTY_PROVINCE)
    private String province;

    @NotEmpty(groups = CreateGroup.class, message = ExceptionHandlerConst.EMPTY_COUNTRY)
    private String country;

    @Pattern(groups = CreateGroup.class, regexp = ValidationRegexPattern.PHONE_REGEX, message = ExceptionHandlerConst.INVALID_HOME_PHONE)
    private String homePhone;

    private String businessPhone;
    private String language;

    @NotEmpty(groups = CreateGroup.class, message = ExceptionHandlerConst.EMPTY_USERNAME)
    private String username;

    @JsonDeserialize(using = PreferredModeOfCommunicationDeserializer.class)
    private PreferredModeOfCommunication preferredModeOfCommunication;
}
