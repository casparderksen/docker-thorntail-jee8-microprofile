# About

This is a microservices chassis for building applications with JEE8/MicroProfile/Docker, based on Thorntail.
Datasource and database-specific migration scripts can be selected by specifying a configuration profile.
Unit-integration tests are ran against an H2 in-memory database.

# Integrated frameworks:

- Thorntail 2.2.1
- Docker container built via Fabric8 Docker Maven Plugin
- Remote debugging in Docker container
- Lombok (add plugin to your IDE)
- Datasource with health check
- JAX-RS resource
- TLS (https)
- Flyway database migration
- JPA and transactions
- SLF4J logging and Thorntail logging configuration
- MicroProfile Health endpoint with JVM and system health (via MicroProfile Extensions)
- MicroProfile Metrics endpoint (with example Counter)
- MicroProfile Config configuration
- MicroProfile OpenAPI specification with SwaggerUI extension

# Test frameworks

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

Microprofile extensions:
- Health UI: [http://localhost:8080/health-ui/](http://localhost:8080/health-ui/)
- Swagger UI: [http://localhost:8080/api/openapi-ui](http://localhost:8080/api/openapi-ui)

Resources:
- Ping [http://localhost:8080/api/ping](http://localhost:8080/api/ping)
- Ping counter: [http://localhost:8080/metrics/application/PingCounter](http://localhost:8080/metrics/application/PingCounter)
- Config: [http://localhost:8080/api/config/{key}](http://localhost:8080/api/config/{key})
- CRUD resource example: [http://localhost:8080/api/documents](http://localhost:8080/api/documents)
 
# Building the application

Before building, see the [workaround for Flyway database migrations](#flyway-database-migrations).

# Running the application

Go to directory [`myapp`](myapp).
    
## Running from Maven

Running the application from Maven:
- `mvn thorntail:run`
- `mvn thorntail:start`

Build docker container:
- `mvn package -Pdocker`

Run Docker container:
- `mvn docker:run -Pdocker`
- `mvn docker:start -Pdocker`
- `mvn docker:stop -Pdocker`

## Running from the command line

To run the application from the command line:

    $ java -jar target/myapp-thorntail.jar -Sh2

A profile with datasource configuration must be specified.

## Running from Docker

    $ mvn package -Pdocker
    $ docker run --rm -it -v $(pwd)/security:/opt/security myapp -Sh2 -Shttps
 
## Running from the IDE

To run the application from IntelliJ:
- Edit Run/Debug Configurations
- Add Application configuration
- Set Program arguments: `-Sh2 -Stesting`
- Set Working directory: `$MODULE_WORKING_DIR$`
- Set Use classpath of module: "myapp"

## Running Arquillian unit-integration tests

The `@DefaultDeployment` annotation does not bundle tests dependencies for in-container tests.
Therefore, an Arquillian loadable extension is added via the Java SPI mechanism for adding test
dependencies to the deployment. Note that `@DefaultDeployment` only adds classes in the current package.

The file [`project-stages.yml`](myapp/src/test/resources/project-stages.yml) contains configuration
required for testing, in particular an H2 datasource. In Thorntail 4, this file could be replaced
with profiles that are activated through the `thorntail.profiles` property.


## Running Arquillian tests from the IDE

To run Arquillian integration tests from IntelliJ:
- Edit Run/Debug Configurations
- Add Arquillian Junit configuration
- Select Configure
- Add Manual container configuration
- Set name: "Thorntail 2.2.1"
- Add dependency, select Existing library: "Maven: io.thorntail:arquillian-adapter:2.2.1-Final"

# Configuring the application

## HTTPS

Enable https by specifying the `https` profile:

    $ java -jar target/myapp-thorntail.jar -Sh2 -Shttps
    
See [project-https.yml](myapp/src/main/resources/project-https.yml) for an example https configuration
(adapt to your needs). Https is not configured by default, because storing passwords and certificates
in archives/containers is insecure and not portable across environments. Furthermore, https could be 
offloaded by Nginx or Istio.

To generate a self-signed certificate, run `gen_keystore.sh` in [myapp/security](myapp/security).

To run the Docker container with https enabled, mount a host volume containing `keystore.jsk` at
 `/opt/security` and specify `-Shttps` as command-line argument. The `mvn docker:run -Pdocker`
target is configured for running with https enabled.

## Datasource configuration

Datasource configuration is stored in `profile-*.yml` files, and not enabled by default. In this way,
it is possible to run a standalone application with an H2 in-memory database, or connect to a network database.
A datasource configuration must be supplied in order to run the application, either via a profile or
external configuration file. Available profiles are: `h2`, `postgres`.

## Flyway database migrations

Database specific migration scripts are stored in 
[src/main/resources/db/migration/_\<db\>_](myapp/src/main/resources/db/migration).

In Thorntail 2.2.1 it is not possible to specify alternate locations for database dependent migration scripts.
This will be solved in Thorntail 2.3.0.Final (https://issues.jboss.org/browse/THORN-914).
Workaround:
- Clone `https://github.com/thorntail/thorntail.git` and execute `mvn clean install -DskipTests`
- Set `thorntail-flyway` version to `2.3.0.Final-SNAPSHOT`
 
## Debugging

The `testing` profile enables debug logging.

# Docker

## Java 8 in Docker

When running Java 8 in a container, the following JVM options should be specified:
- respect CPU and memory limits: `-XX:+UnlockExperimentalVMOptions -XX:+UseCGroupMemoryLimitForHeap`
- use all available heap in Docker: `-XX:MaxRAMFraction=1`
- ensure sufficient entropy: `-Djava.security.egd=file:/dev/./urandom`

When building the container, an exec-style entrypoint should be specified, in order to launch a single process
that can receive Unix signals. In this way, command line arguments for profiles can be specified when starting
the container.

To run the image with another entrypoint:

    docker run --rm -it --entrypoint bash myapp

## Remote debugging in Docker

The `JAVA_TOOL_OPTIONS` environment variable can be specified to set Java command line options without
altering the container image. To enable remote debugging in a Docker container, 
start the container with the following environment variable:

    JAVA_TOOL_OPTIONS=-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5005

The `mvn docker:run -Pdocker` target has been configured for this (see [`pom.xml`](thorntail-parent/pom.xml)).

# References

Thorntail:
- [Thorntail 2.2.1 documentation](https://docs.thorntail.io/2.2.1.Final/)
- [Thorntail examples](https://github.com/thorntail/thorntail-examples/tree/2.2.1.Final)

MicroProfile:
- [MicroProfile Config](https://github.com/eclipse/microprofile-config)
- [MicroProfile Health](https://github.com/eclipse/microprofile-health)
- [MicroProfile JWT](https://github.com/MicroProfileJWT/eclipse-newsletter-sep-2017)
- [MicroProfile Metrics](https://github.com/eclipse/microprofile-metrics/blob/master/spec/src/main/asciidoc/metrics_spec.adoc)
- [MicroProfile Rest Client](https://github.com/eclipse/microprofile-rest-client)
- [MicroProfile OpenAPI](https://github.com/eclipse/microprofile-open-api/blob/master/spec/src/main/asciidoc/microprofile-openapi-spec.adoc)
- [MicroProfile Extensions](https://www.microprofile-ext.org)
- [Swagger UI on MicroProfile OpenAPI](https://www.phillip-kruger.com/post/microprofile_openapi_swaggerui/)
- [Thorntail examples](https://github.com/thorntail/thorntail-examples)

Testing:
- [Functional testing using Drone and Graphene](http://arquillian.org/guides/functional_testing_using_graphene/)
