package nl.casparderksen.util.rest.exceptions;

import javax.persistence.EntityNotFoundException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import static nl.casparderksen.util.rest.Headers.REASON;

/**
 * Maps {@link EntityNotFoundException} to not found response.
 */
@Provider
public class EntityNotFoundExceptionMapper implements ExceptionMapper<EntityNotFoundException> {

    @Override
    public Response toResponse(EntityNotFoundException exception) {
        String reason = exception.getMessage();
        return Response.status(Response.Status.NOT_FOUND).header(REASON, reason).build();
    }
}