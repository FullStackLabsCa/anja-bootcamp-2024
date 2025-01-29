package io.reactivestax.active.life.canada.dto.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import io.reactivestax.active.life.canada.enums.PreferredModeOfCommunication;

import java.io.IOException;

public class PreferredModeOfCommunicationDeserializer extends JsonDeserializer<PreferredModeOfCommunication> {
    @Override
    public PreferredModeOfCommunication deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        String value = jsonParser.getText().trim().toUpperCase().replaceAll("[\\s-]", "_");
        return PreferredModeOfCommunication.valueOf(value);
    }
}
