package com.nbhy.modules.hik.exception;

import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

public class HikException extends RuntimeException{
    private Integer status = INTERNAL_SERVER_ERROR.value();

    public HikException(String msg){
        super(msg);
    }

    public HikException(HttpStatus status, String msg){
        super(msg);
        this.status = status.value();
    }
}
