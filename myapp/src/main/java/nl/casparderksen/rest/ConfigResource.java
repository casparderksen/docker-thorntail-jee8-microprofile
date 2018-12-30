package nl.casparderksen.rest;

import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.NoSuchElementException;

@ApplicationScoped
@Path("/config")
@Tag(name = "Config resource", description = "Exposes application configuration parameters.")
public class ConfigResource {

    @Inject
    private Config config;

    @GET
    @Path("/{key}")
    @Produces(MediaType.TEXT_PLAIN)
    @Operation(description = "Gets the configured value for a key.")
    @APIResponses({
            @APIResponse(responseCode = "200", description = "Success, returns the value"),
            @APIResponse(responseCode = "404", description = "Value not found")
    })
    public String getConfigValue(@PathParam("key") String key) {
        try {
            return config.getValue(key, String.class);
        } catch (NoSuchElementException exception) {
            throw new NotFoundException(exception);
        }
    }
}
