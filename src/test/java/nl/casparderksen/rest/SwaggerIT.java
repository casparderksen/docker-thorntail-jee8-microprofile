package nl.casparderksen.rest;

import lombok.extern.slf4j.Slf4j;
import nl.casparderksen.rest.ArquillianIT;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.drone.api.annotation.Drone;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.WebDriver;
import org.wildfly.swarm.arquillian.DefaultDeployment;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(Arquillian.class)
@DefaultDeployment
@RunAsClient
public class SwaggerIT extends ArquillianIT {

    @Drone
    WebDriver driver;

    @Test
    public void testIt() {
        driver.navigate().to(swaggerUI());
        assertThat(driver.getPageSource()).contains("My application");
    }
}
