package com.smartcampus.exception;

/**
 * Thrown when a request references a resource (e.g., roomId)
 * that does not exist in the system.
 * Mapped to HTTP 422 Unprocessable Entity by its mapper.
 */
public class LinkedResourceNotFoundException extends RuntimeException {
    public LinkedResourceNotFoundException(String message) {
        super(message);
    }
}
