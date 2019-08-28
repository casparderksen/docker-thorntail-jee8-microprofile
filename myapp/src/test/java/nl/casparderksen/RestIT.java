package nl.casparderksen;

import io.restassured.http.ContentType;
import nl.casparderksen.model.Document;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.junit.InSequence;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.wildfly.swarm.arquillian.DefaultDeployment;

import java.net.MalformedURLException;
import java.net.URL;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@SuppressWarnings("ArquillianDeploymentAbsent")
@RunWith(Arquillian.class)
@DefaultDeployment
@RunAsClient
public class RestIT {

    @ArquillianResource
    private URL deploymentURL;

    private URL url(String path) {
        try {
            return new URL(deploymentURL, path);
        } catch (MalformedURLException exception) {
            throw new RuntimeException(exception);
        }
    }

    private URL ping() {
        return url("api/ping");
    }

    private URL health() {
        return url("health");
    }

    private URL metrics() {
        return url("metrics");
    }

    private URL metric(String name) {
        return url("metrics/application/" + name);
    }

    private URL config(String key) {
        return url("api/config/" + key);
    }

    private URL documents() {
        return url("api/documents");
    }

    private URL documents(long id) {
        return url("api/documents/" + id);
    }

    private String relation(URL url, String name) {
        return "<" + url + ">; rel=\"" + name + "\"";
    }

    @Test
    public void healthStatusShouldBeUp() {
        given()
                .when().get(health())
                .then().body("status", equalTo("UP"));
    }

    @Test
    public void healthStatusHeapMemoryShouldBeUp() {
        given()
                .when().get(health())
                .then().body("checks.find{it.name='heap-memory'}.status", equalTo("UP"));
    }

    @Test
    public void healthStatusNonHeapMemoryShouldBeUp() {
        given()
                .when().get(health())
                .then().body("checks.find{it.name='non-heap-memory'}.status", equalTo("UP"));
    }

    @Test
    public void healthStatusMemoryShouldBeUp() {
        given()
                .when().get(health())
                .then().body("checks.find{it.name='memory'}.status", equalTo("UP"));
    }

    @Test
    public void healthStatusThreadsShouldBeUp() {
        given()
                .when().get(health())
                .then().body("checks.find{it.name='threads'}.status", equalTo("UP"));
    }

    @Test
    public void healthStatusSystemLoadShouldBeUp() {
        given()
                .when().get(health())
                .then().body("checks.find{it.name='system-load'}.status", equalTo("UP"));
    }


    @Test
    public void healthStatusLoadShouldBeUp() {
        given()
                .when().get(health())
                .then().body("checks.find{it.name='load'}.status", equalTo("UP"));
    }

    @Test
    public void healthStatusatabaseShouldBeUp() {
        given()
                .when().get(health())
                .then().body("checks.find{it.name='database'}.status", equalTo("UP"));
    }

    @Test
    public void shouldGetMetrics() {
        given()
                .when().get(metrics())
                .then().statusCode(200);
    }

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
    public void shouldGetPingCounter() {
        given()
                .when().get(metric("PingCounter"))
                .then().statusCode(200);
    }

    @Test
    public void shouldGetConfigApplicationName() {
        given().accept(ContentType.TEXT)
                .when().get(config("application.name"))
                .then().statusCode(200)
                .and().body(equalTo("myapp"));
    }

    @Test
    @InSequence(1)
    public void shouldCreateDocument() {
        given().body(new Document("foo"))
                .and().contentType(ContentType.JSON)
                .when().post(documents())
                .then().statusCode(201)
                .and().header("Location", endsWith(documents(1).toString()));
    }

    @Test
    @InSequence(2)
    public void shouldGetDocuments() {
        given()
                .when().get(documents())
                .then().statusCode(200)
                .and().body("size()", equalTo(1));
    }

    @Test
    @InSequence(3)
    public void shouldGetDocument() {
        given()
                .when().get(documents(1))
                .then().statusCode(200)
                .and().header("Link", equalTo(relation(documents(1), "self")))
                .and().body("id", equalTo(1))
                .and().body("name", equalTo("foo"));
    }

    @Test
    @InSequence(4)
    public void shouldUpdateDocument() {
        given().body(new Document("bar"))
                .and().contentType(ContentType.JSON)
                .when().put(documents(1))
                .then().statusCode(200)
                .and().header("Link", equalTo(relation(documents(1), "self")))
                .and().body("id", equalTo(1))
                .and().body("name", equalTo("bar"));
    }

    @Test
    @InSequence(5)
    public void shouldDeleteDocument() {
        given()
                .when().delete(documents(1))
                .then().statusCode(204);
    }

    @Test
    @InSequence(6)
    public void shouldNotFindDocument() {
        given()
                .when().get(documents(1))
                .then().statusCode(404);
    }
}