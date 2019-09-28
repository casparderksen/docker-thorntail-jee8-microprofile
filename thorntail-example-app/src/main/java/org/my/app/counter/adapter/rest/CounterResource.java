package org.my.app.counter.adapter.rest;

import org.eclipse.microprofile.metrics.annotation.Counted;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@ApplicationScoped
@Path("/counter")
@Tag(name = "Counter service", description = "Endpoint for MyCounter counter")
public class CounterResource {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(description = "Increment the MyCounter counter")
    @APIResponse(responseCode = "200", description = "Success")
    @Counted(
            name = "MyCounter",
            absolute = true,
            displayName = "Number of calls to counter service",
            description = "Metric to show how many times the counter endpoint was called")
    public Response counter() {
        return Response.ok().build();
    }
}