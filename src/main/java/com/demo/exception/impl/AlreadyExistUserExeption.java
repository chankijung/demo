package com.demo.exception.impl;

import com.demo.exception.AbstractException;
import org.springframework.http.HttpStatus;

public class AlreadyExistUserExeption extends AbstractException {
    @Override
    public int getStatusCode() {
        return HttpStatus.BAD_REQUEST.value();
    }

    @Override
    public String getMessage() {
        return "이미 존재하는 사용자 명";
    }
}
