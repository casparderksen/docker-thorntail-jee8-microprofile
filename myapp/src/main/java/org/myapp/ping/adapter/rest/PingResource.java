package org.myapp.ping.adapter.rest;

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
@Path("/ping")
@Tag(name = "Ping service", description = "Tests if the API is reachable")
public class PingResource {

    @GET
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Operation(description = "Ping the API")
    @APIResponse(responseCode = "200", description = "Success, API is reachable")
    @Counted(
            name = "PingCounter",
            absolute = true,
            monotonic = true,
            displayName = "Number of pings",
            description = "Metric to show how many times the ping endpoint was called")
    public Response ping() {
        return Response.ok().build();
    }
}