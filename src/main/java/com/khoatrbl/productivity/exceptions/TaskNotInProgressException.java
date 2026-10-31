package com.khoatrbl.productivity.exceptions;

public class TaskNotInProgressException extends RuntimeException {
    public TaskNotInProgressException(String message) {
        super(message);
    }
}
