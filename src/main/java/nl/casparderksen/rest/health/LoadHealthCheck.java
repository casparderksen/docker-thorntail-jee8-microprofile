package nl.casparderksen.rest.health;

import org.eclipse.microprofile.health.Health;
import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;

import javax.enterprise.context.ApplicationScoped;
import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;

@Health
@ApplicationScoped
public class LoadHealthCheck implements HealthCheck {

    @Override
    public HealthCheckResponse call() {
        OperatingSystemMXBean osMXBean = ManagementFactory.getOperatingSystemMXBean();
        int availableProcessors = osMXBean.getAvailableProcessors();
        double loadAverage = osMXBean.getSystemLoadAverage();
        boolean up = (loadAverage < 0.9 * availableProcessors);
        return HealthCheckResponse.named("load")
                .withData("processors", availableProcessors)
                .withData("load", Double.toString(loadAverage))
                .state(up)
                .build();
    }
}