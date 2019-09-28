# About

This is a microservices chassis for building applications with JEE8/MicroProfile/Docker, based on Thorntail.
Datasource and database-specific migration scripts can be selected by specifying a configuration profile.
Unit-integration tests are ran against an H2 in-memory database. A Docker compose example demonstrates
integration with an Oracle database and Prometheus for monitoring.

## Functionality and integrated frameworks

- RedHat UBI8 minimal base image with OpenJDK JRE11
- Maven BOM and parent POM
- Docker container built via Fabric8 Docker Maven Plugin
- Fabric8.io `run-java.sh` entrypoint for JVM tuning and running Java apps in Docker
- Git-commit-id-plugin for runtime application identification (in addition to Maven coordinates)
- Remote debugging in Docker container
- Lombok (add plugin to your IDE)
- MapStruct for mapping between domain values and DTOs (add plugin to your IDE)
- JAX-RS resources with OpenAPI annotations
- Bean Validation of DTOs
- JPA with transactions
- Datasource for H2, Oracle, MySQL, Postgresql
- Flyway database migrations (dependent on selected database)
- SLF4J logging and Thorntail logging configuration
- MicroProfile Health Check provider for the datasource
- MicroProfile Metrics endpoint (with example Counter)
- MicroProfile Config configuration
- MicroProfile Extensions OpenAPI UI
- MicroProfile Extensions Health UI
- MicroProfile Health Extensions for JVM metrics and system health

## Test frameworks

- JUnit5 unit testing
- Arquillian integration testing
- ArchUnit for testing Onion Architecture compliance and independence of functional slices
- Arquillian extension for adding test dependencies (utils, test-frameworks) to in-container tests
- RestAssured integration tests for JAX-RS endpoints
- Selenium browser tests via WebDriver Drone and Graphene
- AssertJ and AssertJ-DB fluent tests

## Observability

- OpenMetrics monitoring with Prometheus. TODO: Graphana dashboards and AlertManager alerts.
- TODO: OpenTracing tracing with Jaeger
- TODO: ElasticStack logging

# Endpoints

MicroProfile:
- Metrics: [http://localhost:8080/metrics](http://localhost:8080/metrics)
- OpenAPI: [http://localhost:8080/openapi](http://localhost:8080/openapi)
- Health: [http://localhost:8080/health](http://localhost:8080/health)

MicroProfile Extension UIs:
- Health UI: [http://localhost:8080/health-ui/](http://localhost:8080/health-ui/)
- Swagger UI: [http://localhost:8080/api/openapi-ui](http://localhost:8080/api/openapi-ui)

Resources:
- Ping with application info [http://localhost:8080/api/ping](http://localhost:8080/api/ping)
- Counter endpoint: [http://localhost:8080/api/counter](http://localhost:8080/api/counter)
- Counter metric: [http://localhost:8080/metrics/application/MyCounter](http://localhost:8080/metrics/application/MyCounter)
- Config: [http://localhost:8080/api/config/{key}](http://localhost:8080/api/config/{key})
- CRUD resource example: [http://localhost:8080/api/documents](http://localhost:8080/api/documents)
 
# Building the application

Build the application with

    $ mvn install

The Maven build uses a BOM for dependency management, and a parent POM for shared build configuration. 
The `install` target is required once to install the dependencies BOM.    
By default, the `h2` Maven profile is enabled for including the H2 in-memory database driver.
    
## Building Docker images

The [`thorntail-docker`](thorntail-docker) directory defines builds for OpenJDK and Thorntail base images.
The base images need to be built once, before building the [`thorntail-example-app`](thorntail-example-app) image.

Build docker images with

    $ mvn package -Pdocker

# Running the application

After building the project, go to the directory [`thorntail-example-app`](thorntail-example-app) for running the application.

##  Running from Maven

To run the application from Maven:

    $ mvn thorntail:run

## Running from the command line

To run the application as fat JAR from the command line:

    $ java -jar target/thorntail-example-app-1.0.0-SNAPSHOT-thorntail.jar -Sh2
    
When running the application, it is mandatory to specify a profile that defines a datasource.
The '-Sh2' option configures a datasource for an embedded H2 in-memory database.

## Running from Docker

To run the application in Docker from Maven:

    $ mvn docker:run -Pdocker
    
To run the application in Docker from the command-line:

    $ docker run --rm -it -p 8080:8080 my/thorntail-example-app
    
## Running from the IDE

To run the application from IntelliJ:
- Edit Run/Debug Configurations
- Add Application configuration
- Set Main class: `org.wildfly.swarm.runner.Runner`
- Set Program arguments: `-Sh2 -Sdebug`
- Set Working directory: `$MODULE_WORKING_DIR$`
- Set Use classpath of module: "thorntail-example-app"
- Check Include dependencies with "Provided" scope
    
# Testing the application

## Architecture conformance tests

We use [ArchUnit](https://www.archunit.org) unit tests for validating compliance to 
[architecture rules](https://github.com/casparderksen/architecture/tree/master).

## Running Arquillian unit-integration tests

We use Arquillian to test the application against an in-memory H2 database.
The file [`project-stages.yml`](thorntail-example-app/src/test/resources/project-stages.yml) contains the configuration
required for testing, in particular an H2 datasource (in Thorntail 4, this file may be removed
and replaced with profiles that are activated through the `thorntail.profiles` property).

The `@DefaultDeployment` annotation is designed to bundle application slices for deployment. 
As a result, only classes in the current package are added to the generated deployment. 
However, slices may depend on generic utility packages. Furthermore, in-container tests may require additional 
testing libraries. For this, an Arquillian loadable extension is added via the Java SPI mechanism for adding utility 
classes and test dependencies to the deployment. If you refactor to different package names or frameworks, do not forget 
to change the package names in [ArquillianExtension](thorntail-example-app/src/test/java/org/my/util/adapter/arquillian/extension/ArquillianExtension.java)
and [org.jboss.arquillian.core.spi.LoadableExtension](thorntail-example-app/src/test/resources/META-INF/services/org.jboss.arquillian.core.spi.LoadableExtension).

## Running Arquillian tests from the IDE

To run Arquillian integration tests from IntelliJ:
- Edit Run/Debug Configurations
- Add Arquillian Junit configuration
- Select Configure
- Add Manual container configuration
- Set name: "Thorntail 2.5.0"
- Add dependency, select Existing library: "Maven: io.thorntail:arquillian-adapter:2.5.0-Final"

# Debugging the application

To enable remote debugging, define the environment variable

    JAVA_TOOL_OPTIONS="-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5005"

The `JAVA_TOOL_OPTIONS` environment variable can also be defined in the Docker environment to enable debugging
without altering the container image.
    
The `profile-debug.yml` profile enables debug logging and configures Hibernate to show SQL queries.
        
# Configuring the application

Configuration profiles are defined in [thorntail-example-app/src/main/resources/`profile-*.yml`](thorntail-example-app/src/main/resources)
Yaml files. No profile defining a datasource is enabled by default. In this way, it is possible to run a standalone 
application with an H2 in-memory database on your workstation, or connect to some network database in other environment
or Docker.

To run the application, a datasource configuration must be provided, either via a Thorntail profile, or via an
external configuration file. Otherwise the application will fail to start.
Available profiles are: `h2`, `oracle`, `mysql` (untested), `postgres` (untested).
 
# Java in Docker

The Fabric8.io `run-java.sh` script is used for tuning JVM options and running the application in Docker.
This allows many JVM settings to be configured via environment variables.
See [https://github.com/fabric8io-images/run-java-sh/blob/master/fish-pepper/run-java-sh/readme.md](https://github.com/fabric8io-images/run-java-sh/blob/master/fish-pepper/run-java-sh/readme.md)
for configuration options. You can add long-living configuration and defaults to the 
[run-env.sh](thorntail-example-app/src/main/docker/run-env.sh) script.

The Docker images are built with an exec-style entrypoint, in order to launch a single process that can receive
Unix signals. In this way, it is possible to specify command line arguments for the entrypoint. 

# Oracle database

TODO: check if Oracle JDBC driver is compatible with JRE11, upgrade if needed.

## Install OJDBC driver

Download `ojdbc8.jar` from
[https://www.oracle.com/database/technologies/appdev/jdbc-ucp-183-downloads.html](https://www.oracle.com/database/technologies/appdev/jdbc-ucp-183-downloads.html)
and install it in your local Maven repository:

    mvn install:install-file -Dfile=ojdbc8.jar -DgroupId=com.oracle.jdbc -DartifactId=ojdbc10 -Dversion=18.3.0.0 -Dpackaging=jar

## Configure the database connection

Configure the JDBC URL in [thorntail-example-app/pom.xml](thorntail-example-app/pom.xml). See the Thorntail documentation for other configuration options.

## Building and running the application

To build and run the application from the command line with an Oracle database:

    $ mvn package -Poracle
    $ java -jar target/thorntail-example-app-1.0.0-SNAPSHOT-thorntail.jar -Soracle
    
## Running from Docker

The `docker` profile in [pom.xml](thorntail-example-app/pom.xml) overrides the JDBC connection URL
for service discovery in a Docker network (adapt to your needs).

To run the application from Docker with an Oracle database:

    $ mvn  package -Pdocker,oracle
    $ docker run --rm -it -p 8080:8080 thorntail-example-app

## Flyway Maven plugin

It is also possible to test database migrations via the Flyway Maven plugin. 

Apply migrations:

    $ mvn flyway:migrate@myschema -Poracle

Clean database:

    $ mvn flyway:clean@myschema -Poracle

# Docker Compose example

The directory [docker-compose](docker-compose) contains a Docker Compose configuration to run a containerized application 
and Oracle database.

## Prerequisites

First build an Oracle container image as described in [https://github.com/oracle/docker-images/tree/master/OracleDatabase/SingleInstance](https://github.com/oracle/docker-images/tree/master/OracleDatabase/SingleInstance). 
For Oracle Database 12.2.0.1 Enterprise Edition this involves the following steps:

1. Place `linuxx64_12201_database.zip` in `dockerfiles/12.2.0.1`.
2. Go to `dockerfiles` and run `buildDockerImage.sh -v 12.2.0.1 -e`

## Build the application

Go to directory [`thorntail-example-app`](thorntail-example-app) and build the application image:
 
     $ mvn package -Pdocker,oracle

## Build the database

Go to the directory [`docker-compose`](docker-compose). First start the database container:

    $ docker-compose up -d oracledb
    $ docker-compose logs -f oracledb

Follow the log file and wait for the database to build. Then start the application container:

    $ docker-compose up -d
    $ docker-compose logs -f thorntail-example-app

# References

Thorntail:
- [Thorntail documentation](https://docs.thorntail.io)
- [Thorntail examples](https://github.com/thorntail/thorntail-examples)

MicroProfile:
- [MicroProfile Config](https://github.com/eclipse/microprofile-config)
- [MicroProfile Health](https://github.com/eclipse/microprofile-health)
- [MicroProfile JWT](https://github.com/MicroProfileJWT/eclipse-newsletter-sep-2017)
- [MicroProfile Metrics](https://github.com/eclipse/microprofile-metrics/blob/master/spec/src/main/asciidoc/metrics_spec.adoc)
- [MicroProfile Rest Client](https://github.com/eclipse/microprofile-rest-client)
- [MicroProfile OpenAPI](https://github.com/eclipse/microprofile-open-api/blob/master/spec/src/main/asciidoc/microprofile-openapi-spec.adoc)
- [MicroProfile Extensions](https://www.microprofile-ext.org)
- [Swagger UI on MicroProfile OpenAPI](https://www.phillip-kruger.com/post/microprofile_openapi_swaggerui/)

Java in Docker:
- [run-java-.sh](https://github.com/fabric8io-images/run-java-sh/blob/master/fish-pepper/run-java-sh/readme.md)

Testing:
- [Functional testing using Drone and Graphene](http://arquillian.org/arquillian-graphene/)
- [ArchUnit architecture testing](https://www.archunit.org)

Oracle:
- [OJDBC compatibility](https://www.oracle.com/technetwork/database/enterprise-edition/jdbc-faq-090281.html#01_01)
