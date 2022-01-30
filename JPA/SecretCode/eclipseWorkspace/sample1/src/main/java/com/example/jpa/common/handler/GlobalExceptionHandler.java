package com.example.jpa.common.handler;

import com.example.jpa.common.exception.AuthFailException;
import com.example.jpa.common.model.ResponseResult;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AuthFailException.class)
    public ResponseEntity<?> authFailException(AuthFailException exception) {
        return ResponseResult.fail("[인증 실패]] " + exception.getMessage());
    }
}
