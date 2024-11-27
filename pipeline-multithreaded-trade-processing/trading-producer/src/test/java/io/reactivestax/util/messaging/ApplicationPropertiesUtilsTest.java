package io.reactivestax.util.messaging;

import io.reactivestax.type.exception.SystemInitializationException;
import io.reactivestax.util.ApplicationPropertiesUtils;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ApplicationPropertiesUtilsTest {

    ApplicationPropertiesUtils applicationPropertiesUtils = ApplicationPropertiesUtils.getInstance("applicationTest.properties");

    @Test
    void testApplicationPropertiesUtilsGetMethodsWithValidFile() {
        assertEquals("hibernate", applicationPropertiesUtils.getPersistenceTechnology());
        assertEquals("rabbitmq", applicationPropertiesUtils.getMessagingTechnology());
        assertEquals("accountNumber", applicationPropertiesUtils.getTradeDistributionCriteria());
        assertEquals("round-robin", applicationPropertiesUtils.getTradeDistributionAlgorithm());
    }

    @Test
    void testLoadApplicationPropertiesWithInvalidFile() {
        applicationPropertiesUtils = ApplicationPropertiesUtils.getInstance();
        assertThrows(SystemInitializationException.class,
                () -> applicationPropertiesUtils.loadApplicationProperties("invalid.properties"));
    }
}
