package com.smartcampus.exception;

/**
 * Thrown when deletion of a room is attempted but the room
 * still has sensors assigned to it.
 * Mapped to HTTP 409 Conflict by RoomNotEmptyExceptionMapper.
 */
public class RoomNotEmptyException extends RuntimeException {
    public RoomNotEmptyException(String message) {
        super(message);
    }
}