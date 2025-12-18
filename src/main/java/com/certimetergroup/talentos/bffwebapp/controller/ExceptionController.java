package com.certimetergroup.talentos.bffwebapp.controller;

import com.certimetergroup.talentos.commons.exception.FailureException;
import com.certimetergroup.talentos.commons.response.Response;
import com.certimetergroup.talentos.commons.enumeration.ResponseEnum;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class ExceptionController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @ExceptionHandler(FailureException.class)
    public ResponseEntity<Response<Void>> handleFailureException(FailureException failureException) {
        logger.error(failureException.toString());
        return ResponseEntity.status(failureException.getResponseEnum().httpStatus).body(new Response<>(failureException.getResponseEnum()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Response<Void>> handleException(Exception exception)  {
        logger.error(exception.toString());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new Response<>(ResponseEnum.INTERNAL_SERVER_ERROR));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Response<Map<String, String>>> handleRequestBody(MethodArgumentNotValidException exception) {
        logger.error(exception.toString());
        Map<String, String> errors = new HashMap<>();
        exception.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage())
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Response<>(ResponseEnum.BAD_REQUEST, errors));
    }

    @ExceptionHandler(BindException.class)
    public ResponseEntity<Response<Map<String, String>>> handleBindException(BindException exception) {
        logger.error(exception.toString());
        Map<String, String> errors = new HashMap<>();
        exception.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage())
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Response<>(ResponseEnum.BAD_REQUEST, errors));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Response<Map<String, String>>> handleConstraint(ConstraintViolationException exception) {
        logger.error(exception.toString());
        Map<String,String> errors = new HashMap<>();
        exception.getConstraintViolations().forEach(violation -> {
            String path = violation.getPropertyPath().toString();
            errors.put(path, violation.getMessage());
        });
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Response<>(ResponseEnum.BAD_REQUEST, errors));
    }
}

