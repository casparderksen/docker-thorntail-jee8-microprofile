package nl.casparderksen;

import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.drone.api.annotation.Drone;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.WebDriver;
import org.wildfly.swarm.arquillian.DefaultDeployment;

import static org.assertj.core.api.Assertions.assertThat;

@SuppressWarnings("ArquillianDeploymentAbsent")
@RunWith(Arquillian.class)
@DefaultDeployment
@RunAsClient
public class SwaggerIT extends AbstractRestIT {

    @Drone
    private WebDriver driver;

    @Test
    public void shouldDisplayUI() {
        driver.navigate().to("http://localhost:8080/api/openapi-ui/index.html");
        assertThat(driver.getPageSource()).contains("myapp - Swagger UI");
    }
}
