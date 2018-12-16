package nl.casparderksen.rest;

import org.eclipse.microprofile.config.Config;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.NoSuchElementException;

@Path("/config")
public class ConfigEndpoint {

    @Inject
    private Config config;

    @GET
    @Path("/{key}")
    @Produces(MediaType.TEXT_PLAIN)
    public String getConfigValue(@PathParam("key") String key) {
        try {
            return config.getValue(key, String.class);
        } catch (NoSuchElementException exception) {
            throw new NotFoundException(exception);
        }
    }
}
