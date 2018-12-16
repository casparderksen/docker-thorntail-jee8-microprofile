package nl.casparderksen.rest;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class RuntimeExceptionMapper implements ExceptionMapper<RuntimeException> {

    @Override
    public Response toResponse(RuntimeException exception) {
        if(exception instanceof WebApplicationException) {
            return ((WebApplicationException) exception).getResponse();
        }
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
    }
}
