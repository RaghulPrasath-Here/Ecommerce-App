package com.rodi.Ecommerce_App.exception;

import com.rodi.Ecommerce_App.payload.APIResponse;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;

@RestControllerAdvice
public class MyGlobalExceptionsHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<HashMap<String, String>> myMethodArgumentNotValidException(MethodArgumentNotValidException e){
        HashMap<String, String> response = new HashMap<>();
        e.getBindingResult().getAllErrors().forEach(err ->{
            String fieldName = ((FieldError)err).getField();
            String fieldMsg = err.getDefaultMessage();
            response.put(fieldName, fieldMsg);
        });
        return new ResponseEntity<HashMap<String, String>>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<HashMap<String, String>> myConstraintViolationException(ConstraintViolationException e){
        HashMap<String, String> response = new HashMap<>();
        e.getConstraintViolations().forEach(violation ->{
            String fieldPath = violation.getPropertyPath().toString();
            String fieldName = fieldPath.contains(".")
                    ? fieldPath.substring(fieldPath.lastIndexOf('.') + 1)
                    : fieldPath;
            String message = violation.getMessage();
            response.put(fieldName, message);
        });
        return new ResponseEntity<HashMap<String, String>>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<APIResponse> myResourceNotFoundException(ResourceNotFoundException e){
        String message = e.getMessage();
        APIResponse apiResponse = new APIResponse(message, false);
        return new ResponseEntity<>(apiResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(APIException.class)
    public ResponseEntity<APIResponse> myDuplicateCategoryException(APIException e){
        String message = e.getMessage();
        APIResponse apiResponse = new APIResponse(message, false);
        return new ResponseEntity<>(apiResponse, HttpStatus.BAD_REQUEST);
    }

}
