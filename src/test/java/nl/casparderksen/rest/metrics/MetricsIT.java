package nl.casparderksen.rest.metrics;

import io.restassured.RestAssured;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.wildfly.swarm.arquillian.DefaultDeployment;

import static io.restassured.RestAssured.given;

@RunWith(Arquillian.class)
@DefaultDeployment
@RunAsClient
public class MetricsIT {

    @BeforeClass
    public static void setUp() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.basePath = "/metrics";
        RestAssured.port = 8080;
    }

    @Test
    public void shouldGetMetrics() {
        given()
                .when().get()
                .then().statusCode(200);
    }
}
