package com.smartcampus.filter;

import java.io.IOException;
import java.util.logging.Logger;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.ext.Provider;

/**
 * Logging Filter — logs every incoming request and outgoing response.
 *
 * WHY a filter instead of Logger.info() in every method?
 * Logging is a "cross-cutting concern" — it applies to ALL endpoints.
 * Putting logging in each method duplicates code (if 10 methods,
 * 20+ log statements scattered everywhere). If the log format changes,
 * you edit 20 places. A single filter covers everything automatically,
 * including new endpoints added in the future.
 *
 * @Provider — auto-registered by Jersey's package scanning.
 * Implements BOTH interfaces: runs on every request AND every response.
 */
@Provider
public class LoggingFilter implements ContainerRequestFilter, ContainerResponseFilter {

    private static final Logger LOGGER = Logger.getLogger(LoggingFilter.class.getName());

    // Runs BEFORE the request reaches any resource method
    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        String method = requestContext.getMethod();
        String uri = requestContext.getUriInfo().getRequestUri().toString();

        LOGGER.info(">>> INCOMING: " + method + " " + uri);
    }

    // Runs AFTER the response is assembled, BEFORE it's sent to the client
    @Override
    public void filter(ContainerRequestContext requestContext,
                       ContainerResponseContext responseContext) throws IOException {
        int status = responseContext.getStatus();
        String method = requestContext.getMethod();
        String uri = requestContext.getUriInfo().getRequestUri().toString();

        LOGGER.info("<<< OUTGOING: " + status + " | " + method + " " + uri);
    }
}