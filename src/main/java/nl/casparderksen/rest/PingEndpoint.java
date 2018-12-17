package nl.casparderksen.rest;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.metrics.MetricUnits;
import org.eclipse.microprofile.metrics.annotation.Counted;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.xml.bind.annotation.XmlRootElement;

@Path("/ping")
public class PingEndpoint {

    @Inject
    @ConfigProperty(name = "application.name")
    private String name;

    @Inject
    @ConfigProperty(name = "application.version")
    private String version;

    @Counted(unit = MetricUnits.NONE,
            name = "nrPings",
            absolute = true,
            monotonic = true,
            displayName = "Nr of pings",
            description = "Metric to show how many times the ping endpoint was called.")
    @GET
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public PingResponse doGet() {
        return new PingResponse(name, version);
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @XmlRootElement
    static class PingResponse {
        private String name;
        private String version;
    }
}
