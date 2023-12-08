package io.sultanov.taskmanagementsystem.exceptions;

public class ForbiddenActionException extends RuntimeException {

    public ForbiddenActionException() {
        super();
    }

    public ForbiddenActionException(String message) {
        super(message);
    }
}
