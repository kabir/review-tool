package org.overbaard.review.tool;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.core.Is.is;

import javax.json.Json;

import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import io.restassured.http.Header;


@QuarkusTest
public class ConfigEndpointTest {

    @Test
    public void testListCreateDeleteOrganisations() {
        given()
                .header(new Header("Authorization", "dummy"))
                .when().get("/api/config/organisations")
                .then()
                .statusCode(200)
                .body("size()", is(2))
                .body("[0].id", equalTo(1))
                .body("[0].name", equalTo("My Project"))
                .body("[0].tool-pr-repo", equalTo("myproject-review"))
                .body("[1].id", equalTo(2))
                .body("[1].name", equalTo("Overbård"))
                .body("[1].tool-pr-repo", equalTo("overbaard-review"));

        // Add an organisation
        int id = given()
                .header(new Header("Authorization", "dummy"))
                .contentType(ContentType.JSON)
                .body(Json.createObjectBuilder()
                        .add("name", "New One")
                        .add("tool-pr-repo", "new-review")
                        .build().toString())
                .when().post("/api/config/organisations")
                .then()
                .statusCode(201)
                .body("id", greaterThan(2))
                .body("name", equalTo("New One"))
                .body("tool-pr-repo", equalTo("new-review"))
                .extract().path("id");

        // Check the full list again
        given()
                .header(new Header("Authorization", "dummy"))
                .when().get("/api/config/organisations")
                .then()
                .statusCode(200)
                .body("size()", is(3))
                .body("[0].id", equalTo(1))
                .body("[1].id", equalTo(id))
                .body("[1].name", equalTo("New One"))
                .body("[1].tool-pr-repo", equalTo("new-review"))
                .body("[2].id", equalTo(2))
;

        // Delete the added org
        given()
                .header(new Header("Authorization", "dummy"))
                .when().delete("/api/config/organisations/" + id)
                .then()
                .statusCode(204);

        given()
                .header(new Header("Authorization", "dummy"))
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
                .header(new Header("Authorization", "dummy"))
                .when().get("/api/config/organisations/1")
                .then()
                .statusCode(200)
                .body("id", equalTo(1))
                .body("name", equalTo("My Project"))
                .body("tool-pr-repo", equalTo("myproject-review"));

        given()
                .header(new Header("Authorization", "dummy"))
                .when().get("/api/config/organisations/2")
                .then()
                .statusCode(200)
                .body("id", equalTo(2))
                .body("name", equalTo("Overbård"))
                .body("tool-pr-repo", equalTo("overbaard-review"));

    }

    @Test
    public void testUpdateOrganisation() {
        given()
                .header(new Header("Authorization", "dummy"))
                .contentType(ContentType.JSON)
                .body(Json.createObjectBuilder()
                        .add("name", "Overdone")
                        .add("tool-pr-repo", "overlord")
                        .build().toString())
                .when().put("/api/config/organisations/2")
                .then()
                .statusCode(200)
                .body("id", equalTo(2))
                .body("name", equalTo("Overdone"))
                .body("tool-pr-repo", equalTo("overlord"));

        given()
                .header(new Header("Authorization", "dummy"))
                .contentType(ContentType.JSON)
                .body(Json.createObjectBuilder()
                        .add("name", "Overbård")
                        .add("tool-pr-repo", "overbaard-review")
                        .build().toString())
                .when().put("/api/config/organisations/2")
                .then()
                .statusCode(200);
    }



}
