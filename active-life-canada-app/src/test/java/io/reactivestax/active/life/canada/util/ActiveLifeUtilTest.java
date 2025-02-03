package io.reactivestax.active.life.canada.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.reactivestax.active.life.canada.constant.TestData;
import io.reactivestax.active.life.canada.exception.SomethingWentWrongException;
import io.reactivestax.active.life.canada.model.SecurityHeader;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ActiveLifeUtilTest {

    @InjectMocks
    private ActiveLifeUtil activeLifeUtil;

    @Mock
    private ObjectMapper objectMapper;

    @Test
    void testGetSecurityHeader_Success() throws JsonProcessingException {
        String securityHeaderJson = TestData.SECURITY_HEADER_JSON;
        SecurityHeader expectedHeader = new SecurityHeader(TestData.LOGGED_IN_MEMBER_ID_STRING);

        when(objectMapper.readValue(securityHeaderJson, SecurityHeader.class)).thenReturn(expectedHeader);
        SecurityHeader result = activeLifeUtil.getSecurityHeader(securityHeaderJson);

        assertNotNull(result);
        assertEquals(TestData.LOGGED_IN_MEMBER_ID_STRING, result.getFamilyMemberId());
        verify(objectMapper, times(1)).readValue(securityHeaderJson, SecurityHeader.class);
    }

    @Test
    void testGetSecurityHeader_ExceptionThrown() throws JsonProcessingException {
        String securityHeaderJson = TestData.SECURITY_HEADER_JSON;

        when(objectMapper.readValue(securityHeaderJson, SecurityHeader.class))
                .thenThrow(new JsonProcessingException("Error parsing JSON") {
                });

        assertThrows(SomethingWentWrongException.class, () -> activeLifeUtil.getSecurityHeader(securityHeaderJson));
        verify(objectMapper, times(1)).readValue(securityHeaderJson, SecurityHeader.class);
    }

    @Test
    void testCompareDateAndTime_MoreThanOneDayDifference() {
        LocalDate date = LocalDate.now().minusDays(2);
        LocalTime time = LocalTime.now().minusMinutes(10);
        boolean result = activeLifeUtil.compareDateAndTime(date, time);

        assertTrue(result);
    }

    @Test
    void testCompareDateAndTime_LessThanOneDayDifference() {
        LocalDate date = LocalDate.now();
        LocalTime time = LocalTime.now().minusMinutes(5);
        boolean result = activeLifeUtil.compareDateAndTime(date, time);

        assertFalse(result);
    }

    @Test
    void testCompareDateAndTime_SameDay() {
        LocalDate date = LocalDate.now();
        LocalTime time = LocalTime.now().minusMinutes(30);
        boolean result = activeLifeUtil.compareDateAndTime(date, time);

        assertFalse(result);
    }
}
