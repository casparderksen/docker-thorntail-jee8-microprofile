package org.my.app.config.adapter.rest;

import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.enums.ParameterIn;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;


@ApplicationScoped
@Path("/config")
@Tag(name = "Config resource", description = "Exposes application configuration parameters")
public class ConfigResource {

    @Inject
    private Config config;

    @GET
    @Path("/{key}")
    @Produces(MediaType.TEXT_PLAIN)
    @Operation(description = "Gets the configured value for a key")
    @Parameter(name = "key", description = "Name of the configured value", in = ParameterIn.PATH, required = true, schema = @Schema(type = SchemaType.STRING))
    @APIResponse(responseCode = "200", description = "Success, returns the value", content = @Content(mediaType = "text/plain"))
    @APIResponse(responseCode = "404", description = "Value not found")
    public String getConfigValue(@PathParam("key") String key) {
        if (isForbidden(key)) {
            throw new NotAcceptableException();
        }
        return config.getValue(key, String.class);
    }

    private boolean isForbidden(String key) {
        final String lowerCaseKey = key.toLowerCase();
        return lowerCaseKey.contains("secret") || lowerCaseKey.contains("password") || lowerCaseKey.contains("private");
    }
}
