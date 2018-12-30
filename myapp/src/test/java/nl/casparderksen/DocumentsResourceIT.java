package nl.casparderksen;

import io.restassured.http.ContentType;
import nl.casparderksen.model.Document;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.junit.InSequence;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.wildfly.swarm.arquillian.DefaultDeployment;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.equalTo;

@SuppressWarnings("ArquillianDeploymentAbsent")
@RunWith(Arquillian.class)
@DefaultDeployment
@RunAsClient
public class DocumentsResourceIT extends AbstractRestIT {

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
