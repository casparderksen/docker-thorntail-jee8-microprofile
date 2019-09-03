package org.my.util.rest.exceptions;

import lombok.extern.slf4j.Slf4j;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

/**
 * Maps sneaky thrown {@link Exception} to internal server error, in order to avoid returning stack traces.
 */
@Slf4j
@Provider
public class SneakyExceptionMapper implements javax.ws.rs.ext.ExceptionMapper<Exception> {

    @Override
    public Response toResponse(Exception exception) {
        log.error("internal server error", exception);
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
    }
}