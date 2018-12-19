package nl.casparderksen.rest;

import io.restassured.http.ContentType;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.wildfly.swarm.arquillian.DefaultDeployment;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasXPath;

@RunWith(Arquillian.class)
@DefaultDeployment
@RunAsClient
public class RestApplicationIT extends AbstractArquillianIT {

    @Test
    public void shouldPingJson() {
        given().accept(ContentType.JSON)
                .when().get(ping())
                .then().statusCode(200)
                .and().body("name", equalTo("myapp"));
    }

    @Test
    public void shouldPingXml() {
        given().accept(ContentType.XML)
                .when().get(ping())
                .then().statusCode(200)
                .and().body(hasXPath("//name", equalTo("myapp")));
    }

    @Test
    public void shouldGetConfigName() {
        given().accept(ContentType.TEXT)
                .when().get(config("application.name"))
                .then().statusCode(200)
                .and().body(equalTo("myapp"));
    }
}