package io.reactivestax.ems.exception;

import io.reactivestax.ems.constant.ExceptionHandlerConstant;
import io.reactivestax.ems.constant.ValidationMessage;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler globalExceptionHandler;

    @BeforeAll()
    void setUp() {
        globalExceptionHandler = new GlobalExceptionHandler();
    }

    @Test
    void testHandleInvalidRequestException() {
        InvalidRequestException invalidRequestException = new InvalidRequestException(ValidationMessage.INVALID_OTP);
        ResponseEntity<Map<String, String>> mapResponseEntity = globalExceptionHandler.handleInvalidRequestException(invalidRequestException);
        assertThat(mapResponseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        Map<String, String> body = mapResponseEntity.getBody();
        if (body != null) {
            org.assertj.core.api.Assertions.assertThat(body).containsEntry(ExceptionHandlerConstant.CODE,
                    String.valueOf(HttpStatus.BAD_REQUEST.value()));
            org.assertj.core.api.Assertions.assertThat(body).containsEntry(ExceptionHandlerConstant.TYPE,
                    HttpStatus.BAD_REQUEST.getReasonPhrase());
            org.assertj.core.api.Assertions.assertThat(body.get(ExceptionHandlerConstant.TIMESTAMP)).isNotNull();
            org.assertj.core.api.Assertions.assertThat(body).containsEntry(ExceptionHandlerConstant.MESSAGE,
                    ValidationMessage.INVALID_OTP);
        }
    }

    @Test
    void testHandleTooManyRequestsException() {
        TooManyRequestsException tooManyRequestsException =
                new TooManyRequestsException(ValidationMessage.OTP_VERIFICATION_FAILED_WITH_ATTEMPTS_EXCEEDED);
        ResponseEntity<Map<String, String>> mapResponseEntity =
                globalExceptionHandler.handleTooManyRequestsException(tooManyRequestsException);
        assertThat(mapResponseEntity.getStatusCode()).isEqualTo(HttpStatus.TOO_MANY_REQUESTS);
        Map<String, String> body = mapResponseEntity.getBody();
        if (body != null) {
            org.assertj.core.api.Assertions.assertThat(body).containsEntry(ExceptionHandlerConstant.CODE,
                    String.valueOf(HttpStatus.TOO_MANY_REQUESTS.value()));
            org.assertj.core.api.Assertions.assertThat(body).containsEntry(ExceptionHandlerConstant.TYPE,
                    HttpStatus.TOO_MANY_REQUESTS.getReasonPhrase());
            org.assertj.core.api.Assertions.assertThat(body.get(ExceptionHandlerConstant.TIMESTAMP)).isNotNull();
            org.assertj.core.api.Assertions.assertThat(body).containsEntry(ExceptionHandlerConstant.MESSAGE,
                    ValidationMessage.OTP_VERIFICATION_FAILED_WITH_ATTEMPTS_EXCEEDED);
        }
    }
}
