package org.my.app.application.adapter.rest;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.eclipse.microprofile.config.Config;

@SuppressWarnings("WeakerAccess")
@Data
@NoArgsConstructor
public class ApplicationInfo {

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

    public ApplicationInfo(Config config) {
        projectGroupId = config.getValue("project.groupId", String.class);
        projectArtifactId = config.getValue("project.artifactId", String.class);
        projectVersion = config.getValue("project.version", String.class);
        gitRemoteOriginURL = config.getValue("git.remote.origin.url", String.class);
        gitBuildTime = config.getValue("git.build.time", String.class);
        gitCommitId = config.getValue("git.commit.id", String.class);
        gitCommitIdAbbrev = config.getValue("git.commit.id.abbrev", String.class);
        gitBranch = config.getValue("git.branch", String.class);
        gitDirty = config.getValue("git.dirty", Boolean.class);
        gitTags = config.getOptionalValue("git.tags", String.class).orElse("");
    }
}
