# About

This is a microservices chassis for building applications with JEE8/MicroProfile/Docker, based on Thorntail.

# Integrated frameworks:

- Thorntail 2.2.1
- Lombok (add plugin to your IDE)
- Integration testing in an Arquillian container
- SLF4J logging and Thorntail logging configuration
- JAX-RS with RestAssured integration tests
- MicroProfile Health endpoint with checks for heap memory and system load (via MXBeans)
- MicroProfile Config configuration
- Docker container built via Fabric8 Docker Maven Plugin
    
- Remote debugging in Docker container

# Maven targets

Starting and stopping the application:
- mvn thorntail:run
- mvn thorntail:stop

As Docker container:
- mvn docker:run -Pdocker
- mvn docker:stop -Pdocker

# Java 8 in Docker

When running Java 8 in a container, the following JVM options should be specified:
- respect CPU and memory limits: `-XX:+UnlockExperimentalVMOptions -XX:+UseCGroupMemoryLimitForHeap`
- use all available heap in Docker: `-XX:MaxRAMFraction=1`
- ensure sufficient entropy: `-Djava.security.egd=file:/dev/./urandom`

When building the container, an exec-style entrypoint should be specified, in order to launch a single process
that can receive Unix signals.
 
# Unit-integration testing from the IDE

To run Arquillian integration tests from IntelliJ:
- Edit Run/Debug Configurations
- Add Arquillian Junit configuration
- Select Configure
- Add Manual container configuration
- Set name: "Thorntail 2.2.1"
 - Add dependency, select Existing library: "Maven: io.thorntail:arquillian-adapter:2.2.1-Final"

# Remote debugging

To enable remote debugging in a Docker container, start the application with the following environment variable:

    JAVA_TOOL_OPTIONS=-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5005

The  `docker:run` Maven target has been configured for this (see `pom.xml`).

