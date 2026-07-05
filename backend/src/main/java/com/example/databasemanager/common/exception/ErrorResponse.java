package com.example.databasemanager.common.exception;

/**
 * Standard JSON error body returned by {@link GlobalExceptionHandler}.
 *
 * @param status    HTTP status code
 * @param message   human-readable error description
 * @param timestamp UTC epoch millis when the error was created
 */
public record ErrorResponse(int status, String message, long timestamp) {

    /** Convenience constructor that sets timestamp to the current instant. */
    public ErrorResponse(int status, String message) {
        this(status, message, System.currentTimeMillis());
    }
}
