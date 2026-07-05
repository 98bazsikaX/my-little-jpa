package com.example.databasemanager.common.exception;

/**
 * Thrown when a unique constraint would be violated, e.g. duplicate username or
 * email. Mapped to HTTP 409 by {@link GlobalExceptionHandler}.
 */
public class DuplicateResourceException extends RuntimeException {

    public DuplicateResourceException(String message) {
        super(message);
    }
}
