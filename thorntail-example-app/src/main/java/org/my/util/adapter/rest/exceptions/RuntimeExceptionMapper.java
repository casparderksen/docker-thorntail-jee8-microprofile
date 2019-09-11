package org.my.util.adapter.rest.exceptions;

import lombok.extern.slf4j.Slf4j;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 * Maps uncaught {@link RuntimeException} to internal server error, in order to avoid returning stack traces.
 * If the exception is a {@link WebApplicationException}, its response is returned.
 * Otherwise, the exception is logged an internal server error response is returned.
 */
@Slf4j
@Provider
public class RuntimeExceptionMapper implements ExceptionMapper<RuntimeException> {

    @Override
    public Response toResponse(RuntimeException exception) {
        if (exception instanceof WebApplicationException) {
            return ((WebApplicationException) exception).getResponse();
        }
        log.error("internal server error", exception);
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
    }
}