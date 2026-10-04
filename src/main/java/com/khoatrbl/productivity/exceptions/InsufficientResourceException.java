package com.khoatrbl.productivity.exceptions;

public class InsufficientResourceException extends RuntimeException {
    public InsufficientResourceException(String message) {
        super(message);
    }
}
