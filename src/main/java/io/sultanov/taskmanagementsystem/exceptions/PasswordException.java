package io.sultanov.taskmanagementsystem.exceptions;

public class PasswordException extends RuntimeException {
    public PasswordException() {
        super();
    }

    public PasswordException(String message) {
        super(message);
    }
}
