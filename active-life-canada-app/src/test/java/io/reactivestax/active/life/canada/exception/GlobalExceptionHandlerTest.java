package io.reactivestax.active.life.canada.exception;

import io.reactivestax.active.life.canada.constant.ExceptionHandlerConst;
import io.reactivestax.active.life.canada.constant.TestData;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class GlobalExceptionHandlerTest {

    @InjectMocks
    private GlobalExceptionHandler globalExceptionHandler;

    @Mock
    private MethodArgumentNotValidException methodArgumentNotValidException;

    @Mock
    private BindingResult bindingResult;

    @Test
    void testHandleValidationException() {
        FieldError fieldError = new FieldError(TestData.USERNAME, TestData.USERNAME, ExceptionHandlerConst.INCORRECT_USERNAME_PASSWORD);
        when(methodArgumentNotValidException.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getFieldErrors()).thenReturn(java.util.Collections.singletonList(fieldError));

        ResponseEntity<Map<String, String>> responseEntity = globalExceptionHandler.handleValidationException(methodArgumentNotValidException);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        Map<String, String> body = responseEntity.getBody();
        assert body != null;
        assertThat(body).containsEntry(ExceptionHandlerConst.CODE, String.valueOf(HttpStatus.BAD_REQUEST.value()))
                .containsEntry(ExceptionHandlerConst.TYPE, HttpStatus.BAD_REQUEST.getReasonPhrase())
                .containsEntry(TestData.USERNAME, ExceptionHandlerConst.INCORRECT_USERNAME_PASSWORD);
        assertThat(body.get(ExceptionHandlerConst.TIMESTAMP)).isNotNull();
    }

    @Test
    void testHandleInvalidRequestException() {
        InvalidRequestException invalidRequestException = new InvalidRequestException(ExceptionHandlerConst.INVALID_OFFERED_COURSE_ID);

        ResponseEntity<Map<String, String>> responseEntity = globalExceptionHandler.handleInvalidRequestException(invalidRequestException);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        Map<String, String> body = responseEntity.getBody();
        assert body != null;
        assertThat(body).containsEntry(ExceptionHandlerConst.CODE, String.valueOf(HttpStatus.BAD_REQUEST.value()))
                .containsEntry(ExceptionHandlerConst.TYPE, HttpStatus.BAD_REQUEST.getReasonPhrase())
                .containsEntry(ExceptionHandlerConst.MESSAGE, ExceptionHandlerConst.INVALID_OFFERED_COURSE_ID);
        assertThat(body.get(ExceptionHandlerConst.TIMESTAMP)).isNotNull();
    }

    @Test
    void testHandleSomethingWentWrongException() {
        SomethingWentWrongException exception = new SomethingWentWrongException(ExceptionHandlerConst.INTERNAL_ERROR);

        ResponseEntity<Map<String, String>> responseEntity = globalExceptionHandler.handleSomethingWentWrongException(exception);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        Map<String, String> body = responseEntity.getBody();
        assert body != null;
        assertThat(body).containsEntry(ExceptionHandlerConst.CODE, String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR.value()))
                .containsEntry(ExceptionHandlerConst.TYPE, HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase())
                .containsEntry(ExceptionHandlerConst.MESSAGE, ExceptionHandlerConst.INTERNAL_ERROR);
        assertThat(body.get(ExceptionHandlerConst.TIMESTAMP)).isNotNull();
    }

    @Test
    void testHandleUnauthorizedAccessException() {
        UnauthorizedAccessException exception = new UnauthorizedAccessException(ExceptionHandlerConst.UNAUTHORIZED_ACCESS);

        ResponseEntity<Map<String, String>> responseEntity = globalExceptionHandler.handleUnauthorizedAccessException(exception);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        Map<String, String> body = responseEntity.getBody();
        assert body != null;
        assertThat(body).containsEntry(ExceptionHandlerConst.CODE, String.valueOf(HttpStatus.UNAUTHORIZED.value()))
                .containsEntry(ExceptionHandlerConst.TYPE, HttpStatus.UNAUTHORIZED.getReasonPhrase())
                .containsEntry(ExceptionHandlerConst.MESSAGE, ExceptionHandlerConst.UNAUTHORIZED_ACCESS);
        assertThat(body.get(ExceptionHandlerConst.TIMESTAMP)).isNotNull();
    }
}
