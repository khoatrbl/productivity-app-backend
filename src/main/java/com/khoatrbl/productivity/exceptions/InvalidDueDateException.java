package com.khoatrbl.productivity.exceptions;

public class InvalidDueDateException extends RuntimeException {
    public InvalidDueDateException(String message) {
        super(message);
    }
}
