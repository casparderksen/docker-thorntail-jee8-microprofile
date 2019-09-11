package org.my.util.adapter.rest.exceptions;

import org.my.util.adapter.rest.Headers;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import java.util.NoSuchElementException;

/**
 * Maps {@link NoSuchElementException} to not found response.
 */
@Provider
public class NoSuchElementExceptionMapper implements ExceptionMapper<NoSuchElementException> {

    @Override
    public Response toResponse(NoSuchElementException exception) {
        String reason = exception.getMessage();
        return Response.status(Response.Status.NOT_FOUND).header(Headers.REASON, reason).build();
    }
}