package com.example.demo.controller;

import com.example.demo.dto.common.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<?>> handleIllegalArgumentException(IllegalArgumentException e) {
        return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<?>> handleValidationException(MethodArgumentNotValidException e) {
        String errorMessage = e.getBindingResult()
                .getFieldErrors()
                .stream()
                .findFirst()
                .map(error -> error.getDefaultMessage())
                .orElse("유효성 검사에 실패했습니다.");

        return ResponseEntity.badRequest()
                .body(ApiResponse.error(errorMessage));
    }
}
