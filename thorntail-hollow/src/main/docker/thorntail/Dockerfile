FROM caspard/openjdk8

LABEL org.label-schema.description="Thorntail JDK8 base image" \
      org.label-schema.name=${project.artifactId} \
      org.label-schema.version=${project.version}

ENV THORNTAIL_HOME=/opt/thorntail

# Create Thorntail user and group used to launch processes
RUN groupadd -r thorntail -g 1000 \
    && useradd -u 1000 -r -g thorntail -m -d ${THORNTAIL_HOME} -s /sbin/nologin -c "Thorntail user" thorntail \
    && chmod 755 ${THORNTAIL_HOME}

# Switch to restricted user
USER thorntail

# Generate self signed TLS certificate
RUN mkdir ${THORNTAIL_HOME}/security \
    && keytool -genkeypair \
           -keyalg RSA \
           -keysize 1024 \
           -validity 365 \
           -alias localhost \
           -dname "CN=localhost" \
           -storetype pkcs12 \
           -keystore ${THORNTAIL_HOME}/security/keystore.jks \
           -keypass changeit \
           -storepass changeit

# TODO Copy hollow jar (renamed to artifactId)
#COPY maven/${project.artifactId}.jar ${THORNTAIL_HOME}/

# Set the working directory to thorntail user home directory
WORKDIR ${THORNTAIL_HOME}

# Expose debug and HTTPS port
EXPOSE 5005 8080 8443
