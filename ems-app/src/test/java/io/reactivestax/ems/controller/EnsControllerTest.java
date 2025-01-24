package io.reactivestax.ems.controller;

import io.reactivestax.ems.constant.SuccessMessage;
import io.reactivestax.ems.dto.BaseDTO;
import io.reactivestax.ems.enums.NotificationMethod;
import io.reactivestax.ems.service.EnsService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(EnsController.class)
class EnsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private EnsService ensService;

    @Test
    void testSendSmsWithValidValues() throws Exception {
        doNothing().when(ensService).save(any(BaseDTO.class), any(NotificationMethod.class));

        mockMvc.perform(post("/api/v1/ems/sms")
                        .content(getPhoneRequestJson())
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(getResponseJson()));
    }

    @Test
    void testSendCallWithValidValues() throws Exception {
        doNothing().when(ensService).save(any(BaseDTO.class), any(NotificationMethod.class));

        mockMvc.perform(post("/api/v1/ems/call")
                        .content(getPhoneRequestJson())
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(getResponseJson()));
    }

    @Test
    void testSendEmailWithValidValues() throws Exception {
        doNothing().when(ensService).save(any(BaseDTO.class), any(NotificationMethod.class));

        mockMvc.perform(post("/api/v1/ems/email")
                        .content(getEmailRequestJson())
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(getResponseJson()));
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

    private String getEmailRequestJson() {
        return """
                  {
                    "customerId": "b87ce6bd-ec3c-4d51-9c48-b68ef99301ee",
                    "email": "example@test.com",
                    "message": "My message."
                  }
                """;
    }

    private BaseDTO getPhoneRequestDTO() {
        return BaseDTO.builder()
                .customerId("b87ce6bd-ec3c-4d51-9c48-b68ef99301ee")
                .phoneNumber("+12233446677")
                .message("My message.")
                .build();
    }

    private String getResponseJson() {
        return """
                  {
                    "message": "%s"
                  }
                """.formatted(SuccessMessage.SUCCESS_ENS_MESSAGE);
    }
}
