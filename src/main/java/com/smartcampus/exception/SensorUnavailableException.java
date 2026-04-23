package com.smartcampus.exception;

/**
 * Thrown when a reading POST is attempted on a sensor that
 * is not ACTIVE (MAINTENANCE or OFFLINE).
 * Mapped to HTTP 403 Forbidden by its mapper.
 */
public class SensorUnavailableException extends RuntimeException {
    public SensorUnavailableException(String message) {
        super(message);
    }
}