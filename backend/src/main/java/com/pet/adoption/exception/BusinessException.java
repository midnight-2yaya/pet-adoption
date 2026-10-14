package com.pet.adoption.exception;

public class BusinessException extends RuntimeException {
    public Integer code;

    public BusinessException(String message) {
        super(message);
        this.code = 400;
    }

    public BusinessException(Integer code, String message) {
        super(message);
        this.code = code;
    }
}