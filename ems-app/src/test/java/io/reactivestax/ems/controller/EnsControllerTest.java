package io.reactivestax.ems.controller;

import io.reactivestax.ems.constant.Endpoints;
import io.reactivestax.ems.constant.SuccessMessage;
import io.reactivestax.ems.constant.ValidationMessage;
import io.reactivestax.ems.dto.BaseDTO;
import io.reactivestax.ems.enums.NotificationMethod;
import io.reactivestax.ems.service.EnsService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(EnsController.class)
class EnsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private EnsService ensService;

    @Test
    void testSendSmsWithValidValues() throws Exception {
        doNothing().when(ensService).save(any(BaseDTO.class), any(NotificationMethod.class));

        mockMvc.perform(post(Endpoints.ENS_BASE + Endpoints.SMS)
                        .content(getPhoneRequestJson())
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(getResponseJson()));
    }

    @Test
    void testSendSmsWithInValidValuesAndEmptyPhoneNumber() throws Exception {
        String invalidPhoneRequestJson = """
                  {
                    "customerId": "",
                    "phoneNumber": "",
                    "message": ""
                  }
                """;

        mockMvc.perform(post(Endpoints.ENS_BASE + Endpoints.SMS)
                        .content(invalidPhoneRequestJson)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(HttpStatus.BAD_REQUEST.value()))
                .andExpect(jsonPath("$.type").value(HttpStatus.BAD_REQUEST.getReasonPhrase()))
                .andExpect(jsonPath("$.customerId").value(ValidationMessage.EMPTY_CUSTOMER_ID))
                .andExpect(jsonPath("$.message").value(ValidationMessage.EMPTY_MESSAGE));
    }

    @Test
    void testSendSmsWithInValidValuesAndInvalidPhoneNumber() throws Exception {
        String invalidPhoneRequestJson = """
                  {
                    "customerId": "",
                    "phoneNumber": "+1243434",
                    "message": ""
                  }
                """;

        mockMvc.perform(post(Endpoints.ENS_BASE + Endpoints.SMS)
                        .content(invalidPhoneRequestJson)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(HttpStatus.BAD_REQUEST.value()))
                .andExpect(jsonPath("$.type").value(HttpStatus.BAD_REQUEST.getReasonPhrase()))
                .andExpect(jsonPath("$.customerId").value(ValidationMessage.EMPTY_CUSTOMER_ID))
                .andExpect(jsonPath("$.message").value(ValidationMessage.EMPTY_MESSAGE));
    }

    @Test
    void testSendCallWithValidValues() throws Exception {
        doNothing().when(ensService).save(any(BaseDTO.class), any(NotificationMethod.class));

        mockMvc.perform(post(Endpoints.ENS_BASE + Endpoints.CALL)
                        .content(getPhoneRequestJson())
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(getResponseJson()));
    }

    @Test
    void testSendEmailWithValidValues() throws Exception {
        String emailRequestJson =  """
                  {
                    "customerId": "b87ce6bd-ec3c-4d51-9c48-b68ef99301ee",
                    "email": "example@test.com",
                    "message": "My message."
                  }
                """;

        doNothing().when(ensService).save(any(BaseDTO.class), any(NotificationMethod.class));

        mockMvc.perform(post(Endpoints.ENS_BASE + Endpoints.EMAIL)
                        .content(emailRequestJson)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(getResponseJson()));
    }

    @Test
    void testSendEmailWithInValidEmail() throws Exception {
        String invalidEmailRequestJson = """
                  {
                    "customerId": "",
                    "email": "invalid_email",
                    "message": ""
                  }
                """;

        mockMvc.perform(post(Endpoints.ENS_BASE + Endpoints.EMAIL)
                        .content(invalidEmailRequestJson)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(HttpStatus.BAD_REQUEST.value()));
    }

    @Test
    void testSendEmailWithEmptyEmail() throws Exception {
        String invalidEmailRequestJson = """
                  {
                    "customerId": "",
                    "email": "",
                    "message": ""
                  }
                """;

        mockMvc.perform(post(Endpoints.ENS_BASE + Endpoints.EMAIL)
                        .content(invalidEmailRequestJson)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(HttpStatus.BAD_REQUEST.value()));
    }

    private String getPhoneRequestJson() {
        return """
                  {
                    "customerId": "b87ce6bd-ec3c-4d51-9c48-b68ef99301ee",
                    "phoneNumber": "+12233446677",
                    "message": "My message."
                  }
                """;
    }

    private String getResponseJson() {
        return """
                  {
                    "message": "%s"
                  }
                """.formatted(SuccessMessage.SUCCESS_ENS_MESSAGE);
    }
}
