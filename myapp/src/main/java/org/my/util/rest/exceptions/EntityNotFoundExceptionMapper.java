package org.my.util.rest.exceptions;

import org.my.util.rest.Headers;

import javax.persistence.EntityNotFoundException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 * Maps {@link EntityNotFoundException} to not found response.
 */
@Provider
public class EntityNotFoundExceptionMapper implements ExceptionMapper<EntityNotFoundException> {

    @Override
    public Response toResponse(EntityNotFoundException exception) {
        String reason = exception.getMessage();
        return Response.status(Response.Status.NOT_FOUND).header(Headers.REASON, reason).build();
    }
}