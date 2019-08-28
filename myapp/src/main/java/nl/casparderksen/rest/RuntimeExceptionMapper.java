package nl.casparderksen.rest;

import lombok.extern.slf4j.Slf4j;

import javax.validation.ConstraintViolationException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
@Slf4j
public class RuntimeExceptionMapper implements ExceptionMapper<RuntimeException> {

    /**
     * Maps uncaught runtime exceptions to internal server errors, in order to avoid returning stack traces.
     * If the exception is a {@link WebApplicationException}, its response is returned.
     * If the exception is a (@link ConstraintViolationException} a bad request status code is returned.
     * Otherwise, an internal server error response is returned and the exception is logged.
     *
     * @param exception the exception
     * @return the response
     */
    @Override
    public Response toResponse(RuntimeException exception) {
        if (exception instanceof WebApplicationException) {
            return ((WebApplicationException) exception).getResponse();
        } else if (exception instanceof ConstraintViolationException) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        log.error("internal server error", exception);
        return Response.serverError().build();
    }
}
