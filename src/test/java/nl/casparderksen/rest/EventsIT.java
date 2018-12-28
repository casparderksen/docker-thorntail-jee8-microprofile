package nl.casparderksen.rest;

import io.restassured.http.ContentType;
import nl.casparderksen.model.Event;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.junit.InSequence;
import org.junit.Test;
import org.junit.runner.RunWith;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.equalTo;

@RunWith(Arquillian.class)
@RunAsClient
public class EventsIT extends AbstractRestIT {

    @Test
    @InSequence(1)
    public void shouldCreateEvent() {
        given().body(new Event("foo"))
                .and().contentType(ContentType.JSON)
                .when().post(events())
                .then().statusCode(201)
                .and().header("Location", endsWith(events(1).toString()));
    }

    @Test
    @InSequence(2)
    public void shouldGetEvents() {
        given()
                .when().get(events())
                .then().statusCode(200)
                .and().body("size()", equalTo(1));
    }

    @Test
    @InSequence(3)
    public void shouldGetEvent() {
        given()
                .when().get(events(1))
                .then().statusCode(200)
                .and().header("Link", equalTo(relation(events(1), "self")))
                .and().body("id", equalTo(1))
                .and().body("name", equalTo("foo"));
    }

    @Test
    @InSequence(4)
    public void shouldUpdateEvent() {
        given().body(new Event("bar"))
                .and().contentType(ContentType.JSON)
                .when().put(events(1))
                .then().statusCode(200)
                .and().header("Link", equalTo(relation(events(1), "self")))
                .and().body("id", equalTo(1))
                .and().body("name", equalTo("bar"));
    }

    @Test
    @InSequence(5)
    public void shouldDeleteEvent() {
        given()
                .when().delete(events(1))
                .then().statusCode(204);
    }

    @Test
    @InSequence(6)
    public void shouldNotFindEvent() {
        given()
                .when().get(events(1))
                .then().statusCode(404);
    }
}
