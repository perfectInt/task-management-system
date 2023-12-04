package io.sultanov.taskmanagementsystem.exceptions;

public class ObjectAlreadyExistsException extends RuntimeException {
    public ObjectAlreadyExistsException() {
        super();
    }

    public ObjectAlreadyExistsException(String message) {
        super(message);
    }
}
