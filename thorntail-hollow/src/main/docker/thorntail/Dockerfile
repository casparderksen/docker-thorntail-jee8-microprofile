FROM caspard/openjdk8

LABEL org.label-schema.description="Thorntail JDK8 base image" \
      org.label-schema.name=${project.artifactId} \
      org.label-schema.version=${project.version}

# Generate self signed TLS certificate
RUN mkdir /opt/security \
    && keytool -genkeypair \
           -keyalg RSA \
           -keysize 1024 \
           -validity 365 \
           -alias localhost \
           -dname "CN=localhost" \
           -storetype pkcs12 \
           -keystore /opt/security/keystore.jks \
           -keypass changeit \
           -storepass changeit

COPY maven/${project.artifactId}.jar /opt
