package io.reactivestax.active.life.canada.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.reactivestax.active.life.canada.constant.ExceptionHandlerConst;
import io.reactivestax.active.life.canada.exception.SomethingWentWrongException;
import io.reactivestax.active.life.canada.model.SecurityHeader;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;

@Component
public class ActiveLifeUtil {
    public SecurityHeader getSecurityHeader(String securityHeaderJson) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.readValue(securityHeaderJson, SecurityHeader.class);
        } catch (JsonProcessingException e) {
            throw new SomethingWentWrongException(ExceptionHandlerConst.INTERNAL_ERROR);
        }
    }

    public boolean compareDateAndTime(LocalDate date, LocalTime time) {
        long betweenDays = ChronoUnit.DAYS.between(date, LocalDate.now());
        long betweenMinutes = ChronoUnit.MINUTES.between(time, LocalTime.now());

        if (betweenDays > 1) {
            return true;
        } else return betweenDays == 1 && betweenMinutes >= 1;
    }
}
