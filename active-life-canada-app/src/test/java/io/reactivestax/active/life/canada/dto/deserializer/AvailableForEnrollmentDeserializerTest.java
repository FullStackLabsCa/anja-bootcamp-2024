package io.reactivestax.active.life.canada.dto.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import io.reactivestax.active.life.canada.enums.AvailableForEnrollment;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AvailableForEnrollmentDeserializerTest {

    private AvailableForEnrollmentDeserializer deserializer;

    @Mock
    private JsonParser jsonParser;

    @Mock
    private DeserializationContext deserializationContext;

    @BeforeEach
    void setUp() {
        deserializer = new AvailableForEnrollmentDeserializer();
    }

    @Test
    void testDeserializeAvailable() throws Exception {
        when(jsonParser.getText()).thenReturn("available");

        AvailableForEnrollment result = deserializer.deserialize(jsonParser, deserializationContext);
        assertEquals(AvailableForEnrollment.AVAILABLE, result);
    }

    @Test
    void testDeserializeWaitlistOpen() throws Exception {
        when(jsonParser.getText()).thenReturn("waitlist open");

        AvailableForEnrollment result = deserializer.deserialize(jsonParser, deserializationContext);
        assertEquals(AvailableForEnrollment.WAITLIST_OPEN, result);
    }

    @Test
    void testDeserializeWithRegexReplacement() throws Exception {
        when(jsonParser.getText()).thenReturn("not-available");

        AvailableForEnrollment result = deserializer.deserialize(jsonParser, deserializationContext);
        assertEquals(AvailableForEnrollment.NOT_AVAILABLE, result);
    }
}
