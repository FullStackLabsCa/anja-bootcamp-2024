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
    private ApplicationPropertiesUtils applicationPropertiesUtilsMock;

    @AfterEach
    void tearDown() {
        Mockito.reset(applicationPropertiesUtilsMock);
    }

    @Test
    void testGetTransactionUtilWithInvalidPersistenceTechnology() {
        try (MockedStatic<ApplicationPropertiesUtils> applicationPropertiesUtilsMockedStatic = mockStatic(ApplicationPropertiesUtils.class)) {
            applicationPropertiesUtilsMockedStatic.when(ApplicationPropertiesUtils::getInstance).thenReturn(applicationPropertiesUtilsMock);
            assertThrows(InvalidPersistenceTechnologyException.class, BeanFactory::getTransactionUtil);
        }
    }

    @Test
    void testGetTradePayloadRepositoryWithInvalidPersistenceTechnology() {
        try (MockedStatic<ApplicationPropertiesUtils> applicationPropertiesUtilsMockedStatic = mockStatic(ApplicationPropertiesUtils.class)) {
            applicationPropertiesUtilsMockedStatic.when(ApplicationPropertiesUtils::getInstance).thenReturn(applicationPropertiesUtilsMock);
            assertThrows(InvalidPersistenceTechnologyException.class, BeanFactory::getTradePayloadRepository);
        }
    }

    @Test
    void testGetLookupSecuritiesRepositoryWithInvalidPersistenceTechnology() {
        try (MockedStatic<ApplicationPropertiesUtils> applicationPropertiesUtilsMockedStatic = mockStatic(ApplicationPropertiesUtils.class)) {
            applicationPropertiesUtilsMockedStatic.when(ApplicationPropertiesUtils::getInstance).thenReturn(applicationPropertiesUtilsMock);
            assertThrows(InvalidPersistenceTechnologyException.class, BeanFactory::getLookupSecuritiesRepository);
        }
    }

    @Test
    void testGetJournalEntryRepositoryWithInvalidPersistenceTechnology() {
        try (MockedStatic<ApplicationPropertiesUtils> applicationPropertiesUtilsMockedStatic = mockStatic(ApplicationPropertiesUtils.class)) {
            applicationPropertiesUtilsMockedStatic.when(ApplicationPropertiesUtils::getInstance).thenReturn(applicationPropertiesUtilsMock);
            assertThrows(InvalidPersistenceTechnologyException.class, BeanFactory::getJournalEntryRepository);
        }
    }

    @Test
    void testGetPositionsRepositoryWithInvalidPersistenceTechnology() {
        try (MockedStatic<ApplicationPropertiesUtils> applicationPropertiesUtilsMockedStatic = mockStatic(ApplicationPropertiesUtils.class)) {
            applicationPropertiesUtilsMockedStatic.when(ApplicationPropertiesUtils::getInstance).thenReturn(applicationPropertiesUtilsMock);
            assertThrows(InvalidPersistenceTechnologyException.class, BeanFactory::getPositionsRepository);
        }
    }

    @Test
    void testGetMessageReceiverWithInvalidMessagingTechnology() {
        try (MockedStatic<ApplicationPropertiesUtils> applicationPropertiesUtilsMockedStatic = mockStatic(ApplicationPropertiesUtils.class)) {
            applicationPropertiesUtilsMockedStatic.when(ApplicationPropertiesUtils::getInstance).thenReturn(applicationPropertiesUtilsMock);
            assertThrows(InvalidMessagingTechnologyException.class, BeanFactory::getMessageReceiver);
        }
    }

    @Test
    void testTransactionRetryerWithInvalidMessagingTechnology() {
        try (MockedStatic<ApplicationPropertiesUtils> applicationPropertiesUtilsMockedStatic = mockStatic(ApplicationPropertiesUtils.class)) {
            applicationPropertiesUtilsMockedStatic.when(ApplicationPropertiesUtils::getInstance).thenReturn(applicationPropertiesUtilsMock);
            assertThrows(InvalidMessagingTechnologyException.class, BeanFactory::getTransactionRetryer);
        }
    }
}
