package org.my.util.adapter.rest.exceptions;

import org.my.util.adapter.rest.Headers;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.util.stream.Collectors;

/**
 * Formats {@link ConstraintViolationException} and returns a bad request response.
 */
@Provider
public class ConstraintViolationExceptionMapper implements ExceptionMapper<ConstraintViolationException> {

    @Override
    public Response toResponse(ConstraintViolationException exception) {
        // Concatenate validation messages with ' and ' in between
        String reason = exception.getConstraintViolations().stream().map(ConstraintViolation::getMessage).collect(Collectors.joining(" and "));
        return Response.status(Response.Status.BAD_REQUEST).header(Headers.REASON, reason).build();
    }
}