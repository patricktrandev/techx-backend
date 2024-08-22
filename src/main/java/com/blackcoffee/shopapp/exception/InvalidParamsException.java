package com.blackcoffee.shopapp.exception;

import org.springframework.web.bind.annotation.ExceptionHandler;

public class InvalidParamsException extends Exception{
    public InvalidParamsException(String message) {
        super(message);
    }

    public InvalidParamsException(String message, Throwable cause) {
        super(message, cause);
    }
}
