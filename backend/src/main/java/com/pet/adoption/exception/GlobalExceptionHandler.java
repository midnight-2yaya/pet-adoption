package com.pet.adoption.exception;

import com.pet.adoption.dto.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ApiResponse<Void> business(BusinessException e) {
        return ApiResponse.fail(e.code, e.getMessage());
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ApiResponse<Void> badCredentials(BadCredentialsException e) {
        return ApiResponse.fail(400, "用户名或密码错误");
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ApiResponse<Void> valid(MethodArgumentNotValidException e) {
        String msg = e.getBindingResult().getFieldErrors().isEmpty()
                ? "参数校验失败"
                : e.getBindingResult().getFieldErrors().get(0).getDefaultMessage();
        return ApiResponse.fail(400, msg);
    }

    @ExceptionHandler(Exception.class)
    public ApiResponse<Void> all(Exception e) {
        log.error("服务器异常", e);
        return ApiResponse.fail(500, "服务器异常：" + e.getMessage());
    }
}