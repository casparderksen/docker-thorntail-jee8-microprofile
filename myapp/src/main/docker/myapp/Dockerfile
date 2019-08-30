FROM caspard/thorntail-openjdk8

LABEL org.label-schema.description="Application ${project.groupId}:${project.artifactId}:${project.version}" \
      org.label-schema.name=${project.artifactId} \
      org.label-schema.version=${project.version}

COPY maven/${project.artifactId}.jar ${THORNTAIL_HOME}/
#COPY maven/${project.artifactId}.war ${THORNTAIL_HOME}/

COPY entrypoint.sh /usr/local/bin/

ENTRYPOINT ["/usr/local/bin/entrypoint.sh"]

