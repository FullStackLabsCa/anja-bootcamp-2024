package io.reactivestax.active.life.canada.enums.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import io.reactivestax.active.life.canada.constant.ShortConstant;
import io.reactivestax.active.life.canada.enums.PreferredModeOfCommunication;

import java.io.IOException;

public class PreferredModeOfCommunicationDeserializer extends JsonDeserializer<PreferredModeOfCommunication> {
    @Override
    public PreferredModeOfCommunication deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        String value = jsonParser.getText().trim().toUpperCase()
                .replaceAll(ShortConstant.ENUM_DESERIALIZER_REGEX, ShortConstant.ENUM_DESERIALIZER_REPLACE);
        return PreferredModeOfCommunication.valueOf(value);
    }
}
