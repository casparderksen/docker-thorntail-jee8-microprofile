package nl.casparderksen.rest;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.metrics.annotation.Counted;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.xml.bind.annotation.XmlRootElement;

@ApplicationScoped
@Path("/ping")
@Tag(name = "Ping service", description = "Tests if the API is reachable.")
public class PingEndpoint {

    @Inject
    @ConfigProperty(name = "application.name")
    private String name;

    @Inject
    @ConfigProperty(name = "application.version")
    private String version;

    @GET
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Operation(description = "Ping the API.")
    @APIResponses({
            @APIResponse(responseCode = "200", description = "Success, API is reachable"),
    })
    @Counted(
            name = "PingCounter",
            absolute = true,
            monotonic = true,
            displayName = "Number of pings",
            description = "Metric to show how many times the ping endpoint was called")
    public PingResponse ping() {
        return new PingResponse(name, version);
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @XmlRootElement
    public static class PingResponse {
        private String name;
        private String version;
    }
}
