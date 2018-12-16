package nl.casparderksen.rest.health;

import io.restassured.RestAssured;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.wildfly.swarm.arquillian.DefaultDeployment;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

@RunWith(Arquillian.class)
@RunAsClient
@DefaultDeployment
public class HealthCheckIT {

    @BeforeClass
    public static void setUp() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.basePath = "health";
        RestAssured.port = 8080;
    }

    @Test
    public void outcomeShouldBeUp() {
        given()
                .when().get()
                .then().body("outcome", equalTo("UP"));
    }

    @Test
    public void memoryShouldBeUp() {
        given()
                .when().get()
                .then().body("checks.find{it.name='memory'}.state", equalTo("UP"));
    }

    @Test
    public void loadShouldBeUp() {
        given()
                .when().get()
                .then().body("checks.find{it.name='load'}.state", equalTo("UP"));
    }
}
