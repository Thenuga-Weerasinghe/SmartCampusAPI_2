package com.smartcampus.exception.mapper;

import com.smartcampus.exception.LinkedResourceNotFoundException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.util.HashMap;
import java.util.Map;

@Provider
public class LinkedResourceNotFoundExceptionMapper
        implements ExceptionMapper<LinkedResourceNotFoundException> {

    @Override
    public Response toResponse(LinkedResourceNotFoundException exception) {
        Map<String, Object> error = new HashMap<>();
        error.put("status", "error");
        error.put("httpCode", 422);
        error.put("errorType", "LINKED_RESOURCE_NOT_FOUND");
        error.put("message", exception.getMessage());
        error.put("hint", "Verify the roomId exists: GET /api/v1/rooms/{roomId}");

        return Response
            .status(422)
            .type(MediaType.APPLICATION_JSON)
            .entity(error)
            .build();
    }
}