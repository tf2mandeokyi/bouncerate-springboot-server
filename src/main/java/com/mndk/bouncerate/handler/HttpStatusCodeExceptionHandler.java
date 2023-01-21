package com.mndk.bouncerate.handler;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.HttpStatusCodeException;

import java.util.Date;

@ControllerAdvice
@SuppressWarnings("unused")
public class HttpStatusCodeExceptionHandler {

    @ExceptionHandler(HttpStatusCodeException.class)
    public ResponseEntity<ExceptionResponse> handleHttpErrorExceptions(HttpStatusCodeException exception) {
        ExceptionResponse exceptionResponse = new ExceptionResponse(
                new Date(), exception.getStatusCode().value(), exception.getMessage()
        );
        return new ResponseEntity<>(exceptionResponse, exception.getStatusCode());
    }

    record ExceptionResponse(
            Date timestamp,
            int status,
            String error
    ) {}
}
