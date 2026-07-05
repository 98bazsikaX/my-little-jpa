package com.example.databasemanager.common.filter;

/**
 * Thrown when a filter criterion references an unknown field name or has an
 * incorrect value type. Mapped to HTTP 400 by {@link com.example.databasemanager.common.exception.GlobalExceptionHandler}.
 */
public class InvalidFilterException extends IllegalArgumentException {

    public InvalidFilterException(String message) {
        super(message);
    }
}
