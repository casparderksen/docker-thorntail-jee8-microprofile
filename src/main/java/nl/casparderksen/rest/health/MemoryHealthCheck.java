package nl.casparderksen.rest.health;

import org.eclipse.microprofile.health.Health;
import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;

import javax.enterprise.context.ApplicationScoped;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;

@Health
@ApplicationScoped
public class MemoryHealthCheck implements HealthCheck {

    @Override
    public HealthCheckResponse call() {
        MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();
        long memoryUsed = memoryMXBean.getHeapMemoryUsage().getUsed();
        long memoryMax = memoryMXBean.getHeapMemoryUsage().getMax();
        boolean up = (memoryUsed < memoryMax * 0.9);
        return HealthCheckResponse.named("memory")
                .withData("used", memoryUsed)
                .withData("max", memoryMax)
                .state(up)
                .build();
    }
}