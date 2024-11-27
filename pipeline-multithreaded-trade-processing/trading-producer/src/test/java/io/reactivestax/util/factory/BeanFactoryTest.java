package io.reactivestax.util.factory;

import io.reactivestax.type.exception.InvalidMessagingTechnologyException;
import io.reactivestax.type.exception.InvalidPersistenceTechnologyException;
import io.reactivestax.util.ApplicationPropertiesUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mockStatic;

@ExtendWith(MockitoExtension.class)
class BeanFactoryTest {

    @Mock
    ApplicationPropertiesUtils applicationPropertiesUtilsMocked;

    @AfterEach
    void tearDown() {
        Mockito.reset(applicationPropertiesUtilsMocked);
    }

    @Test
    void testGetTransactionUtilWithInvalidPersistenceTechnology() {
        try (MockedStatic<ApplicationPropertiesUtils> applicationPropertiesUtilsMockedStatic =
                     mockStatic(ApplicationPropertiesUtils.class)) {
            applicationPropertiesUtilsMockedStatic.when(ApplicationPropertiesUtils::getInstance).thenReturn(applicationPropertiesUtilsMocked);
            assertThrows(InvalidPersistenceTechnologyException.class, BeanFactory::getTransactionUtil);
        }
    }

    @Test
    void testGetTradePayloadRepositoryWithInvalidPersistenceTechnology() {
        try (MockedStatic<ApplicationPropertiesUtils> applicationPropertiesUtilsMockedStatic =
                     mockStatic(ApplicationPropertiesUtils.class)) {
            applicationPropertiesUtilsMockedStatic.when(ApplicationPropertiesUtils::getInstance).thenReturn(applicationPropertiesUtilsMocked);
            assertThrows(InvalidPersistenceTechnologyException.class, BeanFactory::getTradePayloadRepository);
        }
    }

    @Test
    void testGetMessageSenderWithInvalidMessagingTechnology() {
        try (MockedStatic<ApplicationPropertiesUtils> applicationPropertiesUtilsMockedStatic =
                     mockStatic(ApplicationPropertiesUtils.class)) {
            applicationPropertiesUtilsMockedStatic.when(ApplicationPropertiesUtils::getInstance).thenReturn(applicationPropertiesUtilsMocked);
            assertThrows(InvalidMessagingTechnologyException.class, BeanFactory::getMessageSender);
        }
    }
}
