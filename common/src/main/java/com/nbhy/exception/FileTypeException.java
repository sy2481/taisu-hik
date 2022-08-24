package com.nbhy.exception;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

public class FileTypeException extends RuntimeException {
    private Integer status = BAD_REQUEST.value();

    public FileTypeException(String msg){
        super(msg);
    }

//    public FileTypeException(HttpStatus status, String msg){
//        super(msg);
//        this.status = status.value();
//    }

}
