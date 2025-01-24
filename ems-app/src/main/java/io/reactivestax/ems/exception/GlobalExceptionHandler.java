package io.reactivestax.ems.exception;

import io.reactivestax.ems.constant.ExceptionHandlerConstant;
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

        errors.put(ExceptionHandlerConstant.CODE, String.valueOf(httpStatus.value()));
        errors.put(ExceptionHandlerConstant.TYPE, httpStatus.getReasonPhrase());
        errors.put(ExceptionHandlerConstant.TIMESTAMP, String.valueOf(LocalDateTime.now()));

        for (FieldError error : exception.getBindingResult().getFieldErrors()) {
            errors.put(error.getField(), error.getDefaultMessage());
        }

        exception.getBindingResult().getGlobalErrors().forEach(
                error -> errors.put(error.getObjectName(), error.getDefaultMessage()));

        return new ResponseEntity<>(errors, httpStatus);
    }

    @ExceptionHandler(InvalidRequestException.class)
    public ResponseEntity<Map<String, String>> handleInvalidRequestException(InvalidRequestException exception) {
        Map<String, String> errors = new HashMap<>();
        HttpStatus httpStatus = HttpStatus.BAD_REQUEST;

        errors.put(ExceptionHandlerConstant.CODE, String.valueOf(httpStatus.value()));
        errors.put(ExceptionHandlerConstant.TYPE, httpStatus.getReasonPhrase());
        errors.put(ExceptionHandlerConstant.TIMESTAMP, String.valueOf(LocalDateTime.now()));
        errors.put(ExceptionHandlerConstant.MESSAGE, exception.getMessage());

        return new ResponseEntity<>(errors, httpStatus);
    }

    @ExceptionHandler(TooManyRequestsException.class)
    public ResponseEntity<Map<String, String>> handleTooManyRequestsException(TooManyRequestsException exception) {
        Map<String, String> errors = new HashMap<>();
        HttpStatus httpStatus = HttpStatus.TOO_MANY_REQUESTS;

        errors.put(ExceptionHandlerConstant.CODE, String.valueOf(httpStatus.value()));
        errors.put(ExceptionHandlerConstant.TYPE, httpStatus.getReasonPhrase());
        errors.put(ExceptionHandlerConstant.TIMESTAMP, String.valueOf(LocalDateTime.now()));
        errors.put(ExceptionHandlerConstant.MESSAGE, exception.getMessage());

        return new ResponseEntity<>(errors, httpStatus);
    }
}
