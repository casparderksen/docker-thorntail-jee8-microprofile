package nl.casparderksen.rest;

import nl.casparderksen.rest.ArquillianRestIT;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.wildfly.swarm.arquillian.DefaultDeployment;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

@RunWith(Arquillian.class)
@RunAsClient
@DefaultDeployment
public class HealthCheckIT extends ArquillianRestIT {

    @Test
    public void outcomeShouldBeUp() {
        given()
                .when().get(health())
                .then().body("outcome", equalTo("UP"));
    }

    @Test
    public void heapMemoryShouldBeUp() {
        given()
                .when().get(health())
                .then().body("checks.find{it.name='heap-memory'}.state", equalTo("UP"));
    }

    @Test
    public void nonHeapMemoryShouldBeUp() {
        given()
                .when().get(health())
                .then().body("checks.find{it.name='non-heap-memory'}.state", equalTo("UP"));
    }

    @Test
    public void memoryShouldBeUp() {
        given()
                .when().get(health())
                .then().body("checks.find{it.name='memory'}.state", equalTo("UP"));
    }

    @Test
    public void threadsShouldBeUp() {
        given()
                .when().get(health())
                .then().body("checks.find{it.name='threads'}.state", equalTo("UP"));
    }

    @Test
    public void systemLoadShouldBeUp() {
        given()
                .when().get(health())
                .then().body("checks.find{it.name='system-load'}.state", equalTo("UP"));
    }


    @Test
    public void loadShouldBeUp() {
        given()
                .when().get(health())
                .then().body("checks.find{it.name='load'}.state", equalTo("UP"));
    }
}
