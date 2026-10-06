package com.khoatrbl.productivity.exceptions;

public class MaxLevelReachedException extends RuntimeException {
    public MaxLevelReachedException(String message) {
        super(message);
    }
}
