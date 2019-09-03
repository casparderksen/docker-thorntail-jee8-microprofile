package org.my.app;

import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.drone.api.annotation.Drone;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.WebDriver;
import org.wildfly.swarm.arquillian.DefaultDeployment;

import java.net.MalformedURLException;
import java.net.URL;

import static org.assertj.core.api.Assertions.assertThat;

@SuppressWarnings("ArquillianDeploymentAbsent")
@RunWith(Arquillian.class)
@DefaultDeployment
@RunAsClient
public class BrowserIT {

    @Drone
    private WebDriver driver;

    @ArquillianResource
    private URL deploymentURL;

    private URL url(String path) {
        try {
            return new URL(deploymentURL, path);
        } catch (MalformedURLException exception) {
            throw new RuntimeException(exception);
        }
    }

    @Test
    public void shouldDisplaySwaggerUI() {
            driver.navigate().to(url("/api/openapi-ui/index.html"));
        assertThat(driver.getPageSource()).contains("MicroProfile - Open API");
    }
}
