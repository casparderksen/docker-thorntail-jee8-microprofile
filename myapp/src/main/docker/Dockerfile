FROM my/thorntail-server-openjdk8

LABEL org.label-schema.description="Application ${project.groupId}:${project.artifactId}:${project.version}" \
      org.label-schema.name=${project.artifactId} \
      org.label-schema.version=${project.version} \
      org.label-schema.vcs-url=${git.remote.origin.url} \
      org.label-schema.vcs-ref=${git.commit.id}

EXPOSE 5005 8080 8443

COPY run-env.sh /opt/thorntail
COPY maven/project-*.yml /opt/thorntail/
COPY maven/${project.artifactId}.war /opt/thorntail

CMD [ "${project.artifactId}.war", "-s=project-defaults.yml", "-s=project-${databaseProfile}.yml" ]

