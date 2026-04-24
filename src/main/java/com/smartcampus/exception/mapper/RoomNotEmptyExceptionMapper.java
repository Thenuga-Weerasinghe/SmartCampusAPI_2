package com.smartcampus.exception.mapper;

import com.smartcampus.exception.RoomNotEmptyException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.util.HashMap;
import java.util.Map;

/**
 * Intercepts RoomNotEmptyException and converts it to HTTP 409 Conflict.
 
 */
@Provider
public class RoomNotEmptyExceptionMapper implements ExceptionMapper<RoomNotEmptyException> {

    @Override
    public Response toResponse(RoomNotEmptyException exception) {
        Map<String, Object> error = new HashMap<>();
        error.put("status", "error");
        error.put("httpCode", 409);
        error.put("errorType", "ROOM_NOT_EMPTY");
        error.put("message", exception.getMessage());
        error.put("hint", "Decommission all sensors first using DELETE /api/v1/sensors/{id}.");

        return Response
            .status(Response.Status.CONFLICT)
            .type(MediaType.APPLICATION_JSON)
            .entity(error)
            .build();
    }
}