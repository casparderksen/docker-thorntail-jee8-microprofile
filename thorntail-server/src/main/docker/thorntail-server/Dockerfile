FROM my/openjdk8

LABEL org.label-schema.description="Thorntail JDK8 base image" \
      org.label-schema.name=${project.artifactId} \
      org.label-schema.version=${project.version} \
      org.label-schema.vcs-url=${git.remote.origin.url} \
      org.label-schema.vcs-ref=${git.commit.id}

RUN mkdir /opt/thorntail \
    && groupadd -r thorntail -g 1000 \
    && useradd -u 1000 -r -g thorntail -m -d /opt/thorntail -s /sbin/nologin -c "Thorntail user" thorntail

COPY maven/${project.artifactId}.jar /opt/thorntail
ENV JAVA_APP_DIR /opt/thorntail
ENV JAVA_APP_JAR /opt/thorntail/${project.artifactId}.jar

USER thorntail
WORKDIR /opt/thorntail
ENTRYPOINT ["/usr/local/bin/run-java.sh"]
