package com.mndk.bouncerate.handler;

import com.mndk.bouncerate.util.ExceptionResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.HttpStatusCodeException;

import java.util.Date;

@ControllerAdvice
@SuppressWarnings("unused")
public class HttpExceptionHandler {

    @ExceptionHandler(HttpStatusCodeException.class)
    public ResponseEntity<ExceptionResponse> handleStatusCodeExceptions(HttpStatusCodeException exception) {
        ExceptionResponse exceptionResponse = new ExceptionResponse(
                new Date(), exception.getStatusCode().value(), exception.getMessage()
        );
        return new ResponseEntity<>(exceptionResponse, exception.getStatusCode());
    }

}
