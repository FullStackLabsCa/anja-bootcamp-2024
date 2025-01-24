package io.reactivestax.ems.controller;

import io.reactivestax.ems.constant.Endpoints;
import io.reactivestax.ems.constant.SuccessMessage;
import io.reactivestax.ems.constant.ValidationMessage;
import io.reactivestax.ems.dto.BaseDTO;
import io.reactivestax.ems.dto.ValidatedOtpDTO;
import io.reactivestax.ems.dto.VerifyOtpDTO;
import io.reactivestax.ems.enums.NotificationMethod;
import io.reactivestax.ems.service.OtpService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OtpController.class)
class OtpControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private OtpService otpService;

    private final String currentLocalDateTime = LocalDateTime.now().toString();

    @Test
    void testSendSmsOtpWithValidValues() throws Exception {
        doNothing().when(otpService).save(any(BaseDTO.class), any(NotificationMethod.class));

        mockMvc.perform(post(Endpoints.OTP_BASE + Endpoints.SMS)
                        .content(getPhoneRequestJson())
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(getResponseJson(SuccessMessage.SUCCESS_OTP_MESSAGE)));
    }

    @Test
    void testSendCallOtpWithValidValues() throws Exception {
        doNothing().when(otpService).save(any(BaseDTO.class), any(NotificationMethod.class));

        mockMvc.perform(post(Endpoints.OTP_BASE + Endpoints.CALL)
                        .content(getPhoneRequestJson())
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(getResponseJson(SuccessMessage.SUCCESS_OTP_MESSAGE)));
    }

    @Test
    void testSendEmailOtpWithValidValues() throws Exception {
        String emailRequestJson = """
                  {
                    "customerId": "b87ce6bd-ec3c-4d51-9c48-b68ef99301ee",
                    "email": "example@test.com"
                  }
                """;

        doNothing().when(otpService).save(any(BaseDTO.class), any(NotificationMethod.class));

        mockMvc.perform(post(Endpoints.OTP_BASE + Endpoints.EMAIL)
                        .content(emailRequestJson)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(getResponseJson(SuccessMessage.SUCCESS_OTP_MESSAGE)));
    }

    @Test
    void testVerifyOtpWithValidValues() throws Exception {
        String verifyOtpRequestJson = """
                {
                    "customer_id": "b87ce6bd-ec3c-4d51-9c48-b68ef99301ee",
                    "otp": "123456"
                }
                """;

        doNothing().when(otpService).verifyOtp(any(VerifyOtpDTO.class));

        mockMvc.perform(put(Endpoints.OTP_BASE + Endpoints.VERIFY)
                        .content(verifyOtpRequestJson)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(getResponseJson(SuccessMessage.SUCCESS_OTP_VERIFICATION)));
    }

    @Test
    void testVerifyOtpWithEmptyCustomerIdAndEmptyOtp() throws Exception {
        String verifyOtpRequestJson = """
                {
                    "customer_id": "",
                    "otp": ""
                }
                """;

        mockMvc.perform(put(Endpoints.OTP_BASE + Endpoints.VERIFY)
                        .content(verifyOtpRequestJson)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(HttpStatus.BAD_REQUEST.value()))
                .andExpect(jsonPath("$.type").value(HttpStatus.BAD_REQUEST.getReasonPhrase()))
                .andExpect(jsonPath("$.customerId").value(ValidationMessage.EMPTY_CUSTOMER_ID))
                .andExpect(jsonPath("$.otp").value(ValidationMessage.EMPTY_OTP));
    }

    @Test
    void testVerifyOtpWithEmptyCustomerIdAndInvalidOtp() throws Exception {
        String verifyOtpRequestJson = """
                {
                    "customer_id": "",
                    "otp": "12345667"
                }
                """;

        mockMvc.perform(put(Endpoints.OTP_BASE + Endpoints.VERIFY)
                        .content(verifyOtpRequestJson)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(HttpStatus.BAD_REQUEST.value()))
                .andExpect(jsonPath("$.type").value(HttpStatus.BAD_REQUEST.getReasonPhrase()))
                .andExpect(jsonPath("$.customerId").value(ValidationMessage.EMPTY_CUSTOMER_ID))
                .andExpect(jsonPath("$.otp").value(ValidationMessage.INVALID_OTP));
    }

    @Test
    void testStatusOtpWithValidValues() throws Exception {
        String validatedResponseJson = """
                {
                    "message": "%s",
                    "lastValidatedTime": "%s"
                }
                """.formatted(SuccessMessage.SUCCESS_OTP_VALIDATION, currentLocalDateTime);

        doReturn(new ValidatedOtpDTO(SuccessMessage.SUCCESS_OTP_VALIDATION, currentLocalDateTime))
                .when(otpService).status(anyString());

        mockMvc.perform(get(Endpoints.OTP_BASE + "/status/b87ce6bd-ec3c-4d51-9c48-b68ef99301ee"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(validatedResponseJson));
    }

    private String getPhoneRequestJson() {
        return """
                  {
                    "customerId": "b87ce6bd-ec3c-4d51-9c48-b68ef99301ee",
                    "phoneNumber": "+12233446677"
                  }
                """;
    }

    private String getResponseJson(String message) {
        return """
                  {
                    "message": "%s"
                  }
                """.formatted(message);
    }
}
