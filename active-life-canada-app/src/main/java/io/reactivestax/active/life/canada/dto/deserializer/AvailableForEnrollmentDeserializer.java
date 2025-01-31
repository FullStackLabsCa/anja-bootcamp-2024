package io.reactivestax.active.life.canada.dto.deserializer;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import io.reactivestax.active.life.canada.constant.ShortConstant;
import io.reactivestax.active.life.canada.enums.AvailableForEnrollment;

import java.io.IOException;

public class AvailableForEnrollmentDeserializer extends JsonDeserializer<AvailableForEnrollment> {
    @Override
    public AvailableForEnrollment deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JacksonException {
        String value = jsonParser.getText().trim().toUpperCase()
                .replaceAll(ShortConstant.ENUM_DESERIALIZER_REGEX, ShortConstant.ENUM_DESERIALIZER_REPLACE);
        return AvailableForEnrollment.valueOf(value);
    }
}
