package com.n26.exceptions.handlers;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.n26.exceptions.ExpiredTransactionException;
import com.n26.exceptions.FutureTransactionException;
import com.n26.exceptions.InvalidTransactionException;
import com.n26.models.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.io.IOException;
import java.util.Date;

/**
 * Global exception handler for Application and returns appropriate HTTP codes.
 */
@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(ExpiredTransactionException.class)
    public final ResponseEntity<Object> handleExpiredTransactionException(ExpiredTransactionException ex, WebRequest request) {
        ErrorResponse error = new ErrorResponse(new Date(), "Transaction Expired", ex.getLocalizedMessage());
        return new ResponseEntity(error, HttpStatus.NO_CONTENT);
    }

    @ExceptionHandler(IOException.class)
    public final ResponseEntity<Object> handleInvalidJsonException(IOException ex, WebRequest request) {
        ErrorResponse error = new ErrorResponse(new Date(), "Invalid JSON", ex.getLocalizedMessage());
        return new ResponseEntity(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({NumberFormatException.class, InvalidFormatException.class,
            FutureTransactionException.class, JsonMappingException.class, InvalidTransactionException.class})
    public final ResponseEntity<Object> handleUnprocessableEnityExceptions(Exception ex, WebRequest request) {
        ErrorResponse error = new ErrorResponse(new Date(), "Incorrect Number Format", ex.getLocalizedMessage());
        return new ResponseEntity(error, HttpStatus.UNPROCESSABLE_ENTITY);
    }
}
