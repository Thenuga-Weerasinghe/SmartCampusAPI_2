package com.smartcampus.exception.mapper;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The "safety net" — catches ANY exception not handled by
 * a more specific ExceptionMapper.
 */
@Provider
public class GlobalExceptionMapper implements ExceptionMapper<Throwable> {

    private static final Logger LOGGER = Logger.getLogger(GlobalExceptionMapper.class.getName());

    @Override
    public Response toResponse(Throwable exception) {
        // Log full details server-side ONLY
        LOGGER.log(Level.SEVERE,
            "[GLOBAL ERROR] Unexpected error: " + exception.getMessage(),
            exception);

        // Return safe generic response to client
        Map<String, Object> error = new HashMap<>();
        error.put("status", "error");
        error.put("httpCode", 500);
        error.put("errorType", "INTERNAL_SERVER_ERROR");
        error.put("message", "An unexpected internal error occurred. Please contact the system administrator.");
        error.put("support", "admin@smartcampus.westminster.ac.uk");

        return Response
            .status(Response.Status.INTERNAL_SERVER_ERROR)
            .type(MediaType.APPLICATION_JSON)
            .entity(error)
            .build();
    }
}