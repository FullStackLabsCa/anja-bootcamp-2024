package io.reactivestax.active.life.canada.dto.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import io.reactivestax.active.life.canada.enums.PreferredModeOfCommunication;
import io.reactivestax.active.life.canada.enums.deserializer.PreferredModeOfCommunicationDeserializer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PreferredModeOfCommunicationDeserializerTest {

    private PreferredModeOfCommunicationDeserializer preferredModeOfCommunicationDeserializer;

    @Mock
    private JsonParser jsonParser;

    @Mock
    private DeserializationContext deserializationContext;

    @BeforeEach
    void setUp() {
        preferredModeOfCommunicationDeserializer = new PreferredModeOfCommunicationDeserializer();
    }

    @Test
    void testDeserializeEmail() throws Exception {
        when(jsonParser.getText()).thenReturn("email");

        PreferredModeOfCommunication result = preferredModeOfCommunicationDeserializer.deserialize(jsonParser, deserializationContext);
        assertEquals(PreferredModeOfCommunication.EMAIL, result);
    }

    @Test
    void testDeserializeHomePhone() throws Exception {
        when(jsonParser.getText()).thenReturn("home phone");

        PreferredModeOfCommunication result = preferredModeOfCommunicationDeserializer.deserialize(jsonParser, deserializationContext);
        assertEquals(PreferredModeOfCommunication.HOME_PHONE, result);
    }

    @Test
    void testDeserializeBusinessPhone() throws Exception {
        when(jsonParser.getText()).thenReturn("business Phone");

        PreferredModeOfCommunication result = preferredModeOfCommunicationDeserializer.deserialize(jsonParser, deserializationContext);
        assertEquals(PreferredModeOfCommunication.BUSINESS_PHONE, result);
    }

    @Test
    void testDeserializeWithRegexReplacement() throws Exception {
        when(jsonParser.getText()).thenReturn("home-phone");

        PreferredModeOfCommunication result = preferredModeOfCommunicationDeserializer.deserialize(jsonParser, deserializationContext);
        assertEquals(PreferredModeOfCommunication.HOME_PHONE, result);
    }
}
