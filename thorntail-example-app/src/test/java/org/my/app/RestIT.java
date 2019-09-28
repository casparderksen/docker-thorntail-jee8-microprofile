package org.my.app;

import io.restassured.http.ContentType;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.junit.InSequence;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.my.app.documents.adapter.rest.DocumentDTO;
import org.wildfly.swarm.arquillian.DefaultDeployment;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.my.util.adapter.hamcrest.matchers.StoreUuidMatcher.getUUID;
import static org.my.util.adapter.hamcrest.matchers.StoreUuidMatcher.storeUUID;

@SuppressWarnings({"ArquillianDeploymentAbsent", "SameParameterValue"})
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

    private URL counter() {
        return url("api/counter");
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

    private URL documents(UUID id) {
        return url("api/documents/" + id);
    }

    private URL documents(String action) {
        return url("api/documents/" + action);
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
    public void healthStatusDatabaseShouldBeUp() {
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
    public void shouldGetCounterResource() {
        given()
                .when().get(counter())
                .then().statusCode(200);
    }

    @Test
    public void shouldGetCounterMetric() {
        given()
                .when().get(metric("MyCounter"))
                .then().statusCode(200);
    }

    @Test
    public void shouldPingApplication() {
        given()
                .when().get(ping())
                .then().statusCode(200)
                .and().body("projectArtifactId", not(is(emptyOrNullString())));
    }

    @Test
    public void shouldGetConfiguredArtifactId() {
        given().accept(ContentType.TEXT)
                .when().get(config("project.artifactId"))
                .then().statusCode(200)
                .and().body(not(is(emptyOrNullString())));
    }

    @Test
    @InSequence(1)
    public void shouldCreateDocument() {
        given().body(DocumentDTO.builder().name("foo").build())
                .and().contentType(ContentType.JSON)
                .when().post(documents())
                .then().statusCode(201)
                .and().body("id", storeUUID())
                .and().header("Location", endsWith(documents(getUUID()).toString()));
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
                .when().get(documents(getUUID()))
                .then().statusCode(200)
                .and().header("Link", equalTo(relation(documents(getUUID()), "self")))
                .and().body("id", equalTo(getUUID()))
                .and().body("name", equalTo("foo"));
    }

    @Test
    @InSequence(4)
    public void shouldUpdateDocument() {
        given().body(DocumentDTO.builder().name("bar").build())
                .and().contentType(ContentType.JSON)
                .when().put(documents(getUUID()))
                .then().statusCode(200)
                .and().header("Link", equalTo(relation(documents(getUUID()), "self")))
                .and().body("id", equalTo(getUUID()))
                .and().body("name", equalTo("bar"));
    }

    @Test
    @InSequence(5)
    public void shouldCountDocuments() {
        given().accept(ContentType.TEXT)
                .when().get(documents("count"))
                .then().statusCode(200)
                .and().body(equalTo("1"));
    }

    @Test
    @InSequence(6)
    public void shouldDeleteDocument() {
        given()
                .when().delete(documents(getUUID()))
                .then().statusCode(204);
    }

    @Test
    @InSequence(7)
    public void shouldNotGetDocument() {
        given()
                .when().get(documents(getUUID()))
                .then().statusCode(404);
    }

    @Test
    @InSequence(8)
    public void shouldNotUpdateDocument() {
        given().body(DocumentDTO.builder().name("bar").build())
                .and().contentType(ContentType.JSON)
                .when().put(documents(getUUID()))
                .then().statusCode(404);
    }
}