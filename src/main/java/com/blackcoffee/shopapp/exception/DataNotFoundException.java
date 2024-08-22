package com.blackcoffee.shopapp.exception;

import org.springframework.dao.DataIntegrityViolationException;

public class DataNotFoundException extends RuntimeException {
    public DataNotFoundException(String message) {
        super(message);
    }

    public DataNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
