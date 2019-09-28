FROM registry.access.redhat.com/ubi8/ubi-minimal

LABEL org.label-schema.description="Centos7 OpenJDK8 base image" \
      org.label-schema.name=openjdk8 \
      org.label-schema.version=${project.version} \
      org.label-schema.vcs-url=${git.remote.origin.url} \
      org.label-schema.vcs-ref=${git.commit.id}

# Install JRE11
RUN microdnf update \
    && microdnf install --nodocs java-11-openjdk-headless \
    && microdnf clean all

# Set JAVA_HOME environment variable
ENV JAVA_HOME /usr/lib/jvm/java

# Add run-java.sh script
COPY maven/run-java.sh /usr/local/bin

ENV JAVA_APP_DIR /opt
WORKDIR /opt
USER nobody
