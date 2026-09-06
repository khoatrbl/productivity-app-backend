package com.khoatrbl.productivity.exceptions;

public class TaskAccessDeniedException extends RuntimeException {
    public TaskAccessDeniedException() {
        super("You do not have permission to modify this task.");
    }
}
