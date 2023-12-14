package io.mycompany.user.api;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;

import java.util.HashMap;
import java.util.Map;

import static org.springframework.http.ResponseEntity.badRequest;
import static org.springframework.http.ResponseEntity.notFound;

@RestControllerAdvice
@Slf4j
class RestExceptionHandler {

    @ExceptionHandler(UserNotFoundException.class)
    ResponseEntity<String> userNotFoundExceptionHandler(UserNotFoundException ex) {
        log.warn("User with id {} not found", ex.getUserId());
        return notFound().build();
    }

    @ExceptionHandler(WebExchangeBindException.class)
    ResponseEntity<Map<String, String>> invalidUserExceptionHandler(WebExchangeBindException ex) {
        log.warn("Invalid user : {}", ex.getMessage());
        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return badRequest().body(errors);
    }

}
