# About

This is a microservices chassis for building applications with JEE8/MicroProfile/Docker, based on Thorntail.
Datasource and database-specific migration scripts can be selected by specifying a configuration profile.
Unit-integration tests are ran against an H2 in-memory database.

Caveat: https has not been tested for this version yet.

# Integrated frameworks:

- Thorntail
- Docker container built via Fabric8 Docker Maven Plugin
- Fabric8.io run-java.sh entrypoint for tuning and running Java apps in Docker
- Git info in Docker labels and application info (git-commit-id-plugin)
- Remote debugging in Docker container
- Lombok (add plugin to your IDE)
- MapStruct for mapping between domain values and DTOs (add plugin to your IDE)
- JAX-RS resources
- TLS (https)
- JPA and transactions
- Datasource for H2, Oracle and other databases
- Flyway database migration (multiple database flavors)
- SLF4J logging and Thorntail logging configuration
- HealthCheck for datasource
- MicroProfile Health endpoint with JVM, system health (via MicroProfile Extensions)
- MicroProfile Metrics endpoint (with example Counter)
- MicroProfile Config configuration
- MicroProfile OpenAPI specification with SwaggerUI extension

# Test frameworks

- ArchUnit for testing Onion Architecture and dependency rules
- Arquillian integration testing
- Arquillian extension for adding test dependencies (AssertJ) to in-container test
- RestAssured integration tests for JAX-RS endpoints
- Selenium browser tests via Drone and Graphene
- AssertJ and AssertJ-DB fluent tests

# Endpoints

MicroProfile:
- Metrics: [http://localhost:8080/metrics](http://localhost:8080/metrics)
- OpenAPI: [http://localhost:8080/openapi](http://localhost:8080/openapi)
- Health: [http://localhost:8080/health](http://localhost:8080/health)

MicroProfile extensions:
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
    
or

    $ mvn package -Pdocker
    
to build Docker images as well. By default, the `h2` profile is activated to include the H2 in-memory
database driver.

Note that the [`thorntail-hollow`](thorntail-hollow) module
defines base images that need to build once. Afterwards, you can build Docker images from
the [`myapp`](myapp) directory as well.

# Running the application

Go to directory [`myapp`](myapp). To run the application from Maven:

    $ mvn thorntail:run
    
To run from the command line:

    $ java -jar target/myapp-thorntail.jar -Sh2
    
The '-Sh2' option configures a H2 datasource. When using a different database, make sure to enable
the profile for including a matching database driver during the build phase.

## Running from Docker

Make sure that you have built the base image for the application.

To run the application in Docker from Maven:

    $ mvn docker:run -Pdocker,h2
    
To run the application in Docker from the command-line:

    $ mvn package -Pdocker,h2
    $ docker run --rm -it -p 8080:8080 caspard/myapp -Sh2
 
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

The application is organized into slices and then layers, according to 
[Onion Architecture](https://jeffreypalermo.com/2008/07/the-onion-architecture-part-1/) principles.
We use [ArchUnit](https://www.archunit.org) unit tests for validating compliance to architecture rules.

Code organization:
- `org.my.app.<slice>`: slice of functionality (not a layer)
  - `domain`: core logic, depends on business domain, framework-free
    - `model`: domain entities (behaviour and encapsulated data)
    - `service`: domain services
  - `application`: use cases (application specific workflow)
  - `adapter`: adapters for external systems or infrastructure
    - `rest`: REST API (thin, no logic other than DTO mapping and building responses)
- `org.my.util.<slice>`: application independent generic logic (could be moved to library)

Dependency rules:
- Slices must be independent
- There must not be cyclic dependencies
- The `domain` package may not depend on other packages
- The `application` package may depend on `domain`, but not on any `adapter`
- An `adapter` package may use `application` and `domain`
- An `adapter` package may not depend on any other `adapter` package
- A `util` package may not depend on `app` packages

Consequences:
- Use application services to send data from an input adapter to an output adapter (workflow).
- Use Dependency Inversion or domain events for invoking adapters from domain or application services.

We want domain and JPA entities to be consistent at all times.
Therefore, REST adapters use DTOs as input and output models.

## Running Arquillian unit-integration tests

We use Arquillian to test the application against an in-memory H2 database.
The file [`project-stages.yml`](myapp/src/test/resources/project-stages.yml) contains the configuration
required for testing, in particular an H2 datasource. In Thorntail 4, this file may be removed
and replaced with profiles that are activated through the `thorntail.profiles` property.

The `@DefaultDeployment` annotation is designed to bundle application slices for deployment. 
As a result, only classes in the current package are added to the generated deployment. 
However, slices may depend on generic utilities. Furthermore, in-container tests may require additional testing libraries.
For this, an Arquillian loadable extension is added via the Java SPI mechanism for adding utilities and
test dependencies to the deployment. If you refactor to different package names, do not forget to change
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

# Application profiles

## HTTPS

Enable https by specifying the `https` profile:

    $ java -jar target/myapp-thorntail.jar -Sh2 -Shttps
    
See [project-https.yml](myapp/src/main/resources/project-https.yml) for an example https configuration
(adapt to your needs). Https is not configured by default, because storing passwords and certificates
in archives/containers is insecure and not portable across environments. Furthermore, https could be 
offloaded by Nginx, or Istio when deploying to Kubernetes.

To generate a self-signed certificate, run `gen_keystore.sh` in [myapp/security](myapp/security).

To run the Docker container with https enabled, mount a host volume containing `keystore.jsk` at
 `/opt/security` and specify `-Shttps` as command-line argument. The `mvn docker:run -Pdocker`
target is configured for running with https enabled.

## Datasources

Datasource configuration is stored in `profile-\<db\>.yml` files. These profiles are not enabled by default. In this way,
it is possible to run a standalone application with an H2 in-memory database, or connect to a network database.
A datasource configuration must be supplied in order to run the application, either via a profile or via
external configuration file. Available profiles are: `h2`, `oracle`, `mysql` (untested), `postgres` (untested).
 
## Debugging

The `debug` profile enables debug logging.

# Docker

## Java 8 in Docker

The Fabric8.io `run-java.sh` script is used for tuning JVM options and running the application in Docker.
This allows many JVM settings to be configured via environment variables.
See [https://github.com/fabric8io-images/run-java-sh/blob/master/fish-pepper/run-java-sh/readme.md](https://github.com/fabric8io-images/run-java-sh/blob/master/fish-pepper/run-java-sh/readme.md)
for configuration options.

When building the container, an exec-style entrypoint must be specified, in order to launch a single process
that can receive Unix signals. In this way, command line arguments for profiles can be specified when starting
the container. To run the image with another entrypoint:

    $ docker run --rm -it --entrypoint bash caspard/myapp

## Remote debugging in Docker

The `JAVA_TOOL_OPTIONS` environment variable can be specified to set Java command line options without
altering the container image. To enable remote debugging in a Docker container, 
start the container with the following environment variable:

    JAVA_TOOL_OPTIONS=-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5005

The `mvn docker:run -Pdocker` target has been configured for this (see [`pom.xml`](thorntail-parent/pom.xml)).

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

Go to directory [`myapp`](myapp). To build the application as follows:
 
     $ mvn package -Pdocker,oracle,\!h2

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

Testing:
- [Functional testing using Drone and Graphene](http://arquillian.org/arquillian-graphene/)

Oracle:
- [OJDBC compatibility](https://www.oracle.com/technetwork/database/enterprise-edition/jdbc-faq-090281.html#01_01)
