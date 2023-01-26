package com.mndk.bouncerate.handler;

import com.mndk.bouncerate.util.ExceptionResponse;
import com.mysql.cj.jdbc.exceptions.MysqlDataTruncation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Date;

@ControllerAdvice
@SuppressWarnings("unused")
public class MysqlExceptionHandler {

    @ExceptionHandler(MysqlDataTruncation.class)
    public ResponseEntity<ExceptionResponse> handleTruncationException(MysqlDataTruncation exception) {
        ExceptionResponse exceptionResponse = new ExceptionResponse(
                new Date(), 400, "Data or string too long"
        );
        return new ResponseEntity<>(exceptionResponse, HttpStatus.BAD_REQUEST);
    }

}
