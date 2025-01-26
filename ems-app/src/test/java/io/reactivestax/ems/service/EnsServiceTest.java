package io.reactivestax.ems.service;

import io.reactivestax.ems.constant.ValidationMessage;
import io.reactivestax.ems.domain.Customer;
import io.reactivestax.ems.domain.EnsMessage;
import io.reactivestax.ems.dto.BaseDTO;
import io.reactivestax.ems.enums.NotificationMethod;
import io.reactivestax.ems.exception.InvalidRequestException;
import io.reactivestax.ems.messaging.MessageProducer;
import io.reactivestax.ems.repository.EnsRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@SpringBootTest
class EnsServiceTest {

    @Autowired
    private EnsService ensService;

    @MockitoBean
    private EmsCommonService emsCommonService;

    @MockitoBean
    private EnsRepository ensRepository;

    @MockitoBean
    private MessageProducer messageProducer;

    @Test
    void testSaveForPositiveCase() {
        Customer customer = new Customer();
        String uuid = "b87ce6bd-ec3c-4d51-9c48-b68ef99301ee";
        String contact = "+12223334444";
        BaseDTO baseDTO = new BaseDTO(uuid, contact, "", "message");
        when(emsCommonService.checkIfCustomerExists(anyString())).thenReturn(customer);
        when(emsCommonService.getContactValue(anyString(), anyString(), any(NotificationMethod.class))).thenReturn(contact);
        when(emsCommonService.checkIfProvidedContactExistInContacts(anyString(), any())).thenReturn(true);
        when(ensRepository.save(any(EnsMessage.class)))
                .thenReturn(EnsMessage.builder()
                        .id(UUID.fromString(uuid))
                        .build());
        doNothing().when(messageProducer).sendMessageToQueue(anyString(), any(UUID.class));
        ensService.save(baseDTO, NotificationMethod.SMS);
        Mockito.verify(emsCommonService, times(1)).checkIfCustomerExists(uuid);
    }

    @Test
    void testSaveForException() {
        Customer customer = new Customer();
        String uuid = "b87ce6bd-ec3c-4d51-9c48-b68ef99301ee";
        String contact = "+12223334444";
        BaseDTO baseDTO = new BaseDTO(uuid, contact, "", "message");
        when(emsCommonService.checkIfCustomerExists(anyString())).thenReturn(customer);
        when(emsCommonService.getContactValue(anyString(), anyString(), any(NotificationMethod.class))).thenReturn(contact);
        when(emsCommonService.checkIfProvidedContactExistInContacts(anyString(), any())).thenReturn(false);
        assertThatThrownBy(() -> ensService.save(baseDTO, NotificationMethod.SMS),
                ValidationMessage.INVALID_PHONE_NUMBER, InvalidRequestException.class);
    }
}
