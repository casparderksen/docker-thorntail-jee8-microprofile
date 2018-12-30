package nl.casparderksen;

import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.junit.InSequence;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.wildfly.swarm.arquillian.DefaultDeployment;

import static io.restassured.RestAssured.given;

@SuppressWarnings("ArquillianDeploymentAbsent")
@RunWith(Arquillian.class)
@DefaultDeployment
@RunAsClient
public class MetricsIT extends AbstractRestIT {

    @Test
    public void shouldGetMetrics() {
        given()
                .when().get(metrics())
                .then().statusCode(200);
    }

    @Test
    @InSequence(1)
    public void shouldRegisterPingCounter() {
        given()
                .when().get(ping())
                .then().statusCode(200);
    }

    @Test
    @InSequence(2)
    public void shouldGetPingCounter() {
        given()
                .when().get(metric("PingCounter"))
                .then().statusCode(200);
    }
}
