package nl.casparderksen.util.rest.exceptions;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.util.stream.Collectors;

import static nl.casparderksen.util.rest.Headers.REASON;

/**
 * Formats {@link ConstraintViolationException} and returns a bad request response.
 */
@Provider
public class ConstraintViolationExceptionMapper implements ExceptionMapper<ConstraintViolationException> {

    @Override
    public Response toResponse(ConstraintViolationException exception) {
        // Concatenate validation messages with ' and ' in between
        String reason = exception.getConstraintViolations().stream().map(ConstraintViolation::getMessage).collect(Collectors.joining(" and "));
        return Response.status(Response.Status.BAD_REQUEST).header(REASON, reason).build();
    }
}