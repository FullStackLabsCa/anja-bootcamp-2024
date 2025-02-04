package io.reactivestax.active.life.canada.dto.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import io.reactivestax.active.life.canada.enums.Gender;
import io.reactivestax.active.life.canada.enums.deserializer.GenderDeserializer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GenderDeserializerTest {

    private GenderDeserializer genderDeserializer;

    @Mock
    private JsonParser jsonParser;

    @Mock
    private DeserializationContext deserializationContext;

    @BeforeEach
    void setUp() {
        genderDeserializer = new GenderDeserializer();
    }

    @Test
    void testDeserializeMale() throws Exception {
        when(jsonParser.getText()).thenReturn("male");

        Gender result = genderDeserializer.deserialize(jsonParser, deserializationContext);
        assertEquals(Gender.MALE, result);
    }

    @Test
    void testDeserializeFemale() throws Exception {
        when(jsonParser.getText()).thenReturn("female");

        Gender result = genderDeserializer.deserialize(jsonParser, deserializationContext);
        assertEquals(Gender.FEMALE, result);
    }

    @Test
    void testDeserializeTransgender() throws Exception {
        when(jsonParser.getText()).thenReturn("transgender");

        Gender result = genderDeserializer.deserialize(jsonParser, deserializationContext);
        assertEquals(Gender.TRANSGENDER, result);
    }

    @Test
    void testDeserializeNonBinary() throws Exception {
        when(jsonParser.getText()).thenReturn("non-binary");

        Gender result = genderDeserializer.deserialize(jsonParser, deserializationContext);
        assertEquals(Gender.NON_BINARY, result);
    }

    @Test
    void testDeserializeWithRegexReplacement() throws Exception {
        when(jsonParser.getText()).thenReturn("non binary");

        Gender result = genderDeserializer.deserialize(jsonParser, deserializationContext);
        assertEquals(Gender.NON_BINARY, result);
    }
}
