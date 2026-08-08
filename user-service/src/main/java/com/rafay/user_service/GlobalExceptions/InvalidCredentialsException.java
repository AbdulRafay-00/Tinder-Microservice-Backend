package com.rafay.user_service.GlobalExceptions;


public class InvalidCredentialsException extends RuntimeException {

    public InvalidCredentialsException(String message) {
        super(message);
    }
}