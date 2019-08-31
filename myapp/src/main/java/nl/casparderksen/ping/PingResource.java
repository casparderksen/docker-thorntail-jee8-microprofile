package nl.casparderksen.ping;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.eclipse.microprofile.config.Config;
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
public class PingResource {

    @Inject
    private Config config;

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
    public ApplicationInfo ping() {
        return new ApplicationInfo(
                config.getValue("project.groupId", String.class),
                config.getValue("project.artifactId", String.class),
                config.getValue("project.version", String.class),
                config.getValue("git.remote.origin.url", String.class),
                config.getValue("git.build.time", String.class),
                config.getValue("git.commit.id", String.class),
                config.getValue("git.commit.id.abbrev", String.class),
                config.getValue("git.branch", String.class),
                config.getValue("git.dirty", Boolean.class),
                config.getOptionalValue("git.tags", String.class).orElse(""));
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @XmlRootElement
    public static class ApplicationInfo {
        private String projectGroupId;
        private String projectArtifactId;
        private String projectVersion;
        private String gitRemoteOriginURL;
        private String gitBuildTime;
        private String gitCommitId;
        private String gitCommitIdAbbrev;
        private String gitBranch;
        private boolean gitDirty;
        private String gitTags;
    }
}
