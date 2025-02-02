package io.reactivestax.active.life.canada.exception;

import io.reactivestax.active.life.canada.constant.ExceptionHandlerConst;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationException(MethodArgumentNotValidException exception) {
        Map<String, String> errors = new HashMap<>();
        HttpStatus httpStatus = HttpStatus.BAD_REQUEST;

        errors.put(ExceptionHandlerConst.CODE, String.valueOf(httpStatus.value()));
        errors.put(ExceptionHandlerConst.TYPE, httpStatus.getReasonPhrase());
        errors.put(ExceptionHandlerConst.TIMESTAMP, String.valueOf(LocalDateTime.now()));

        for (FieldError error : exception.getBindingResult().getFieldErrors()) {
            errors.put(error.getField(), error.getDefaultMessage());
        }

        exception.getBindingResult()
                .getGlobalErrors().forEach(error -> errors.put(error.getObjectName(), error.getDefaultMessage()));

        return new ResponseEntity<>(errors, httpStatus);
    }

    @ExceptionHandler(InvalidRequestException.class)
    public ResponseEntity<Map<String, String>> handleInvalidRequestException(InvalidRequestException exception) {
        return getExceptionResponseEntity(HttpStatus.BAD_REQUEST, exception);

    }

    @ExceptionHandler(SomethingWentWrongException.class)
    public ResponseEntity<Map<String, String>> handleSomethingWentWrongException(SomethingWentWrongException exception) {
        return getExceptionResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR, exception);

    }

    @ExceptionHandler(UnauthorizedAccessException.class)
    public ResponseEntity<Map<String, String>> handleUnauthorizedAccessException(UnauthorizedAccessException exception) {
        return getExceptionResponseEntity(HttpStatus.UNAUTHORIZED, exception);
    }

    private ResponseEntity<Map<String, String>> getExceptionResponseEntity(HttpStatus httpStatus, Exception exception) {
        Map<String, String> errors = new HashMap<>();

        errors.put(ExceptionHandlerConst.CODE, String.valueOf(httpStatus.value()));
        errors.put(ExceptionHandlerConst.TYPE, httpStatus.getReasonPhrase());
        errors.put(ExceptionHandlerConst.TIMESTAMP, String.valueOf(LocalDateTime.now()));
        errors.put(ExceptionHandlerConst.MESSAGE, exception.getMessage());

        return new ResponseEntity<>(errors, httpStatus);
    }
}
