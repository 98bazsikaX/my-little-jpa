package com.example.databasemanager.common.exception;

public record ErrorResponse(int status, String message, long timestamp) {

    public ErrorResponse(int status, String message) {
        this(status, message, System.currentTimeMillis());
    }
}
