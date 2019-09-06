# About

This is a microservices chassis for building applications with JEE8/MicroProfile/Docker, based on Thorntail.
Datasource and database-specific migration scripts can be selected by specifying a configuration profile.
Unit-integration tests are ran against an H2 in-memory database. A Docker compose example demonstrates
integration with an Oracle database and Prometheus for monitoring.

## Functionality and integrated frameworks

- Thorntail fat JAR, hollow JAR for Docker build, and hot deployment for development
- Docker container built via Fabric8 Docker Maven Plugin
- Fabric8.io run-java.sh entrypoint for JVM tuning and running Java apps in Docker
- Git info and Maven coordinates in Docker labels and application info resource (git-commit-id-plugin)
- Remote debugging in Docker container
- Lombok (add plugin to your IDE)
- JAX-RS resources with OpenAPI annotations. 
- MapStruct for mapping between domain values and DTOs (add plugin to your IDE)
- Bean Validation of DTOs
- JPA with transactions
- Datasource for H2, Oracle and other databases
- Flyway database migration (multiple database flavors)
- SLF4J logging and Thorntail logging configuration
- HealthCheck provider for the datasource
- MicroProfile Health extensions for JVM metrics and system health
- MicroProfile Metrics endpoint (with example Counter)
- MicroProfile Config configuration
- MicroProfile OpenAPI specification with SwaggerUI extension
- MicroProfile Extensions Health UI
- MicroProfile Extensions Swagger UI based on OpenAPI annotations

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
- Ping [http://localhost:8080/api/ping](http://localhost:8080/api/ping)
- Ping counter: [http://localhost:8080/metrics/application/PingCounter](http://localhost:8080/metrics/application/PingCounter)
- Application info [http://localhost:8080/api/info](http://localhost:8080/api/info)
- Config: [http://localhost:8080/api/config/{key}](http://localhost:8080/api/config/{key})
- CRUD resource example: [http://localhost:8080/api/documents](http://localhost:8080/api/documents)
 
# Building the application

Build the application with

    $ mvn package
    
## Building Docker images

In order to benefit from Docker image layering, we first build a base image that contains a Thorntail Hollow JAR. 
This JAR contains our selected Thorntail fractions and generic dependencies such as database drivers. 
We can then add Thorntail Thin WARs to the base image for building application images. 
The base image needs to be built only once, unless additional Thorntail fractions or provided dependencies are needed. 

In order to build the base image, go to the directory [`thorntail-server`](thorntail-server) and run

    $ mvn clean package -Pdocker,h2,mp-ext
    
The `h2` Maven profile includes the H2 in-memory database driver. The `mp-ext` profile includes several
MicroProfile-Extensions libraries. You

## Building Docker application images

After building the base image, go to the directory [`myapp`](myapp) and run

    $ mvn clean package -Pdocker,\!h2,\!mp-ext
    
to build the application image. Disable profiles for dependencies that are already included in the base image.

# Running the application

After building all Maven modules, go to the directory [`myapp`](myapp) for running the application.

##  Running from Maven

To run the application from Maven:

    $ mvn thorntail:run

## Running from the command line

To run the application as fat JAR from the command line:

    $ java -jar target/myapp-1.0.0-SNAPSHOT-thorntail.jar -Sh2
    
When running the application, it is mandatory to specify a profile that defines a datasource.
The '-Sh2' option configures a datasource for an embedded H2 in-memory database.

## Running from Docker

Make sure that you have built the base images for the application.

To run the application in Docker from Maven:

    $ mvn docker:run -Pdocker
    
To run the application in Docker from the command-line:

    $ mvn package -Pdocker
    $ docker run --rm -it -p 8080:8080 my/myapp
    
A limitation of Thorntail Thin WARs is that it is not possible to load configuration profiles.
Therefore, we must specify the location of the corresponding configuration file as option.
The default command runs thorntail with the H2 configuration.
 
## Running from the IDE

To run the application from IntelliJ:
- Edit Run/Debug Configurations
- Add Application configuration
- Set Main class: `org.wildfly.swarm.runner.Runner`
- Set Program arguments: `-Sh2 -Sdebug`
- Set Working directory: `$MODULE_WORKING_DIR$`
- Set Use classpath of module: "myapp"
- Check Include dependencies with "Provided" scope
    
# Testing the application

## Architecture conformance tests

We use [ArchUnit](https://www.archunit.org) unit tests for validating compliance to [architecture rules](doc/architecture.md).

## Running Arquillian unit-integration tests

We use Arquillian to test the application against an in-memory H2 database.
The file [`project-stages.yml`](myapp/src/test/resources/project-stages.yml) contains the configuration
required for testing, in particular an H2 datasource (in Thorntail 4, this file may be removed
and replaced with profiles that are activated through the `thorntail.profiles` property).

The `@DefaultDeployment` annotation is designed to bundle application slices for deployment. 
As a result, only classes in the current package are added to the generated deployment. 
However, slices may depend on generic utilities. Furthermore, in-container tests may require additional testing libraries.
For this, an Arquillian loadable extension is added via the Java SPI mechanism for adding utility classes and
test dependencies to the deployment. If you refactor to different package names or frameworks, do not forget to change
the package names in [ArquillianExtension](myapp/src/test/java/org/my/util/arquillian/ArquillianExtension.java)
and [org.jboss.arquillian.core.spi.LoadableExtension](myapp/src/test/resources/META-INF/services/org.jboss.arquillian.core.spi.LoadableExtension).

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
without altering the container image. Alternatively, you can enable debugging as follows:

    $ docker run --rm -it -p 8080:8080 my/myapp --debug myapp.war -s=project-debug.yml -s=project-h2.yml
    
The `profile-debug.yml` profile enables debug logging and configures Hibernate to show SQL queries.

## Thorntail Watch and Deploy (TWAD)

Thorntail Watch and Deploy (TWAD) is inspired by Adam Bien's [WAD](http://wad.sh) for fat application servers.
However, Thorntail first builds a fat WAR before generating a thin WAR. This makes the build process quite slow
in comparison with real ThinWARs, and this really obstructs productivity (hence the name TWAD).
Note that in Thorntail 4 you can use thorntail-devtools for hot class reloading, making TWAD obsolete.

The [`twad.sh`](myapp/twad.sh) script builds and deploys a thin WAR to a running Thorntail server whenever a
code change is detected. Before watching the source folder, TWAD first starts a server in the background.

Before using TWAD we need to build a server with the deployment-scanner fraction enabled. For this, 
go to the directory [`thorntail-server`](thorntail-server) and run:
                                                                              
    $ mvn clean package -Pscanner,h2,mp-ext
    
Now go to the directory [`myapp`](myapp) and run

    $ mvn clean package
    $ ./twad.sh
        
# Configuring the application

Configuration profiles are defined in [myapp/src/main/resources/`profile-*.yml`](myapp/src/main/resources) Yaml files.
No profile defining a datasource is enabled by default. In this way, it is possible to run a standalone application with 
an H2 in-memory database on your workstation, or connect to some network database in other environment or Docker.

To run the application, a datasource configuration must be provided, either via a profile, or via an
external configuration file. Otherwise the application will fail to start.
Available profiles are: `h2`, `oracle`, `mysql` (untested), `postgres` (untested).
 
# Java in Docker

The Fabric8.io `run-java.sh` script is used for tuning JVM options and running the application in Docker.
This allows many JVM settings to be configured via environment variables.
See [https://github.com/fabric8io-images/run-java-sh/blob/master/fish-pepper/run-java-sh/readme.md](https://github.com/fabric8io-images/run-java-sh/blob/master/fish-pepper/run-java-sh/readme.md)
for configuration options.

When building the container, an exec-style entrypoint must be specified, in order to launch a single process
that can receive Unix signals. In this way, command line arguments for configuration files can be specified when 
starting the container. To run the image with a Bash shell as entrypoint:

    $ docker run --rm -it --entrypoint bash my/myapp

# Oracle database

## Install OJDBC driver

Download `ojdbc8.jar` from
[https://www.oracle.com/database/technologies/appdev/jdbc-ucp-183-downloads.html](https://www.oracle.com/database/technologies/appdev/jdbc-ucp-183-downloads.html)
and install it in your local Maven repository:

    mvn install:install-file -Dfile=ojdbc8.jar -DgroupId=com.oracle.jdbc -DartifactId=ojdbc10 -Dversion=18.3.0.0 -Dpackaging=jar

## Configure the database connection

Configure the connection details in [project-oracle.yml](myapp/src/main/resources/project-oracle.yml).
Also configure the JDBC URL in [myapp/om.xml](myapp/pom.xml). See the Thorntail documentation
for all configuration options.

## Running the application

To run the application from the command line with an Oracle database:

    $ mvn package -Poracle,\!h2
    $ java -jar target/myapp-thorntail.jar -Soracle
    
## Running from Docker

The `docker` profile in [pom.xml](myapp/pom.xml) overrides the JDBC connection URL
for service discovery in a Docker network (adapt to your needs).

To run the application from Docker with an Oracle database:

    $ mvn  package -Pdocker,oracle,\!h2
    $ docker run --rm -it -p 8080:8080 myapp -Soracle

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

Go to directory [`myapp`](myapp) and build the application image:
 
     $ mvn package -Pdocker,oracle,\!h2,\!mp-ext

## Build the database

Go to the directory [`docker-compose`](docker-compose). First start the database container:

    $ docker-compose up -d oracledb
    $ docker-compose logs -f oracledb

Follow the log file and wait for the database to build. Then start the application container:

    $ docker-compose up -d
    $ docker-compose logs -f myapp

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

Oracle:
- [OJDBC compatibility](https://www.oracle.com/technetwork/database/enterprise-edition/jdbc-faq-090281.html#01_01)
