package com.alexpetro.eadp.exception;

public class EmailAlreadyExistsException extends RuntimeException {

    public EmailAlreadyExistsException(String email) {
        super("Email is already registered: " + email);
    }
}