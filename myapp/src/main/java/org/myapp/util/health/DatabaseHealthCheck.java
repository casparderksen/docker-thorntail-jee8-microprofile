package org.myapp.util.health;

import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.health.Health;
import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.HealthCheckResponseBuilder;

import javax.annotation.Resource;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;

@ApplicationScoped
@Health
@Slf4j
public class DatabaseHealthCheck implements HealthCheck {

    @Resource(lookup = "java:jboss/datasources/MyDS")
    private DataSource datasource;

    @Inject
    @ConfigProperty(name = "health.database-timeout", defaultValue = "1")
    private int timeout;

    @Override
    public HealthCheckResponse call() {
        HealthCheckResponseBuilder responseBuilder = HealthCheckResponse.named("database");
        try (Connection connection = datasource.getConnection()) {
            DatabaseMetaData metaData = connection.getMetaData();
            return responseBuilder
                    .withData("databaseProductName", metaData.getDatabaseProductName())
                    .withData("databaseProductVersion", metaData.getDatabaseProductVersion())
                    .withData("driverName", metaData.getDriverName())
                    .withData("driverVersion", metaData.getDriverVersion())
                    .withData("schema", connection.getSchema())
                    .state(connection.isValid(timeout))
                    .build();
        } catch (SQLException exception) {
            log.error("Database connection error", exception);
            return responseBuilder
                    .withData("errorCode", exception.getErrorCode())
                    .withData("errorMessage", exception.getMessage())
                    .down()
                    .build();
        }
    }
}
