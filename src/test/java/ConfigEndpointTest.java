import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.core.Is.is;

import javax.json.Json;

import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;


@QuarkusTest
public class ConfigEndpointTest {

    @Test
    public void testListCreateDeleteOrganisations() {
        given()
                .when().get("/api/config/organisations")
                .then()
                .statusCode(200)
                .body("size()", is(2))
                .body("[0].id", equalTo(1))
                .body("[0].orgName", equalTo("My Project"))
                .body("[0].toolPrRepo", equalTo("myproject-review"))
                .body("[1].id", equalTo(2))
                .body("[1].orgName", equalTo("Overbård"))
                .body("[1].toolPrRepo", equalTo("overbaard-review"));

        // Add an organisation
        int id = given()
                .contentType(ContentType.JSON)
                .body(Json.createObjectBuilder()
                        .add("orgName", "New One")
                        .add("toolPrRepo", "new-review")
                        .build().toString())
                .when().post("/api/config/organisations")
                .then()
                .statusCode(201)
                .body("id", greaterThan(2))
                .body("orgName", equalTo("New One"))
                .body("toolPrRepo", equalTo("new-review"))
                .extract().path("id");

        // Check the full list again
        given()
                .when().get("/api/config/organisations")
                .then()
                .statusCode(200)
                .body("size()", is(3))
                .body("[0].id", equalTo(1))
                .body("[1].id", equalTo(id))
                .body("[1].orgName", equalTo("New One"))
                .body("[1].toolPrRepo", equalTo("new-review"))
                .body("[2].id", equalTo(2))
;

        // Delete the added org
        given()
                .when().delete("/api/config/organisations/" + id)
                .then()
                .statusCode(204);

        given()
                .when().get("/api/config/organisations")
                .then()
                .statusCode(200)
                .log().all()
                .body("size()", is(2))
                .body("[0].id", equalTo(1))
                .body("[1].id", equalTo(2));
    }

    @Test
    public void testGetOrganisation() {
        given()
                .when().get("/api/config/organisations/1")
                .then()
                .statusCode(200)
                .body("id", equalTo(1))
                .body("orgName", equalTo("My Project"))
                .body("toolPrRepo", equalTo("myproject-review"));

        given()
                .when().get("/api/config/organisations/2")
                .then()
                .statusCode(200)
                .body("id", equalTo(2))
                .body("orgName", equalTo("Overbård"))
                .body("toolPrRepo", equalTo("overbaard-review"));

    }

    @Test
    public void testUpdateOrganisation() {
        given()
                .contentType(ContentType.JSON)
                .body(Json.createObjectBuilder()
                        .add("orgName", "Overdone")
                        .add("toolPrRepo", "overlord")
                        .build().toString())
                .when().put("/api/config/organisations/2")
                .then()
                .statusCode(200)
                .body("id", equalTo(2))
                .body("orgName", equalTo("Overdone"))
                .body("toolPrRepo", equalTo("overlord"));

        given()
                .contentType(ContentType.JSON)
                .body(Json.createObjectBuilder()
                        .add("orgName", "Overbård")
                        .add("toolPrRepo", "overbaard-review")
                        .build().toString())
                .when().put("/api/config/organisations/2")
                .then()
                .statusCode(200);
    }



}
