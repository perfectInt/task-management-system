package io.sultanov.taskmanagementsystem.config;

import io.sultanov.taskmanagementsystem.exceptions.ForbiddenActionException;
import io.sultanov.taskmanagementsystem.exceptions.ObjectAlreadyExistsException;
import io.sultanov.taskmanagementsystem.exceptions.ObjectNotFoundException;
import io.sultanov.taskmanagementsystem.exceptions.PasswordException;
import io.sultanov.taskmanagementsystem.exceptions.dto.ErrorMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class AdviceController {

    @ExceptionHandler(ObjectNotFoundException.class)
    public ResponseEntity<ErrorMessage> handleNotFound(ObjectNotFoundException ex) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ErrorMessage(ex.getMessage()));
    }

    @ExceptionHandler(value = {ObjectAlreadyExistsException.class})
    public ResponseEntity<Object> handleAlreadyExists(ObjectAlreadyExistsException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = {PasswordException.class})
    public ResponseEntity<Object> handlePasswordException(PasswordException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleMethodNotValidException(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return errors;
    }

    @ExceptionHandler(ForbiddenActionException.class)
    public ResponseEntity<ErrorMessage> handleForbiddenActionException(ForbiddenActionException ex) {
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(new ErrorMessage(ex.getMessage()));
    }
}
