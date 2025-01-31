package io.reactivestax.active.life.canada.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.reactivestax.active.life.canada.constant.ExceptionMessage;
import io.reactivestax.active.life.canada.exception.SomethingWentWrongException;
import io.reactivestax.active.life.canada.model.SecurityHeader;
import org.springframework.stereotype.Component;

@Component
public class ActiveLifeUtil {
    public SecurityHeader getSecurityHeader(String securityHeaderJson) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.readValue(securityHeaderJson, SecurityHeader.class);
        } catch (JsonProcessingException e) {
            throw new SomethingWentWrongException(ExceptionMessage.INTERNAL_ERROR);
        }
    }
}
