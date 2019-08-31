package nl.casparderksen.ping;

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
public class PingResource {

    @Inject
    @ConfigProperty(name = "project.groupId")
    private String projectGroupId;

    @Inject
    @ConfigProperty(name = "project.artifactId")
    private String projectArtifactId;

    @Inject
    @ConfigProperty(name = "project.version")
    private String projectVersion;

    @Inject
    @ConfigProperty(name = "git.remote.origin.url")
    private String gitRemoteOriginURL;

    @Inject
    @ConfigProperty(name = "git.build.time")
    private String gitBuildTime;

    @Inject
    @ConfigProperty(name = "git.branch")
    private String gitBranch;

    @Inject
    @ConfigProperty(name = "git.commit.id")
    private String gitCommitId;

    @Inject
    @ConfigProperty(name = "git.commit.id.abbrev")
    private String gitCommitIdAbbrev;

    @Inject
    @ConfigProperty(name = "git.dirty")
    private boolean gitDirty;

    @Inject
    @ConfigProperty(name = "git.tags", defaultValue = "")
    private String gitTags;

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
        return new PingResponse(
                projectGroupId,
                projectArtifactId,
                projectVersion,
                gitRemoteOriginURL,
                gitBuildTime,
                gitBranch,
                gitCommitId,
                gitCommitIdAbbrev,
                gitDirty,
                gitTags);
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @XmlRootElement
    public static class PingResponse {
        private String projectGroupId;
        private String projectArtifactId;
        private String projectVersion;
        private String gitRemoteOriginURL;
        private String gitBuildTime;
        private String gitBranch;
        private String gitCommitId;
        private String gitCommitIdAbbrev;
        private boolean gitDirty;
        private String gitTags;
    }
}
