package org.overbaard.review.tool.config;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.core.Is.is;

import javax.json.Json;
import javax.json.bind.JsonbBuilder;
import javax.json.bind.JsonbConfig;

import org.junit.jupiter.api.Test;
import org.overbaard.review.tool.config.github.MirroredRepository;
import org.overbaard.review.tool.config.github.Organisation;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import io.restassured.http.Header;
import io.restassured.specification.RequestSpecification;


@QuarkusTest
public class ConfigEndpointTest {

    @Test
    public void testListCreateDeleteOrganisations() {
        getBaseRequest()
                .when().get("/api/config/organisations")
                .then()
                .statusCode(200)
                .body("size()", is(2))
                .body("[0].id", equalTo(1))
                .body("[0].name", equalTo("My Project"))
                .body("[0].toolPrRepo", equalTo("myproject-review"))
                .body("[1].id", equalTo(2))
                .body("[1].name", equalTo("Overbård"))
                .body("[1].toolPrRepo", equalTo("overbaard-review"));

        // Add an organisation
        int id = getBaseRequest()
                .body(toJson(new Organisation("New One", "new-review")))
                .when().post("/api/config/organisations")
                .then()
                .statusCode(201)
                .body("id", greaterThan(2))
                .body("name", equalTo("New One"))
                .body("toolPrRepo", equalTo("new-review"))
                .extract().path("id");

        // Check the full list again
        getBaseRequest()
                .when().get("/api/config/organisations")
                .then()
                .statusCode(200)
                .body("size()", is(3))
                .body("[0].id", equalTo(1))
                .body("[1].id", equalTo(id))
                .body("[1].name", equalTo("New One"))
                .body("[1].toolPrRepo", equalTo("new-review"))
                .body("[2].id", equalTo(2));

        // Delete the added org
        getBaseRequest()
                .when().delete("/api/config/organisations/" + id)
                .then()
                .statusCode(204);

        getBaseRequest()
                .when().get("/api/config/organisations")
                .then()
                .statusCode(200)
                .log().all()
                .body("size()", is(2))
                .body("[0].id", equalTo(1))
                .body("[1].id", equalTo(2));
    }

    @Test
    public void testGetOrganisationNoDetail() {
        getBaseRequest()
                .when().get("/api/config/organisations/1")
                .then()
                .statusCode(200)
                .body("id", equalTo(1))
                .body("name", equalTo("My Project"))
                .body("toolPrRepo", equalTo("myproject-review"))
                .body("mirroredRepositories", nullValue());

        getBaseRequest()
                .when().get("/api/config/organisations/2")
                .then()
                .statusCode(200)
                .body("id", equalTo(2))
                .body("name", equalTo("Overbård"))
                .body("toolPrRepo", equalTo("overbaard-review"))
                .body("mirroredRepositories", nullValue());

    }

    @Test
    public void testGetOrganisationWithDetail() {
        getBaseRequest()
                .when().get("/api/config/organisations/1")
                .then()
                .statusCode(200)
                .body("id", equalTo(1))
                .body("name", equalTo("My Project"))
                .body("toolPrRepo", equalTo("myproject-review"))
                .body("mirroredRepositories", nullValue());

        getBaseRequest()
                .queryParam("detail", true)
                .when().get("/api/config/organisations/1")
                .then()
                .statusCode(200)
                .body("id", equalTo(1))
                .body("name", equalTo("My Project"))
                .body("toolPrRepo", equalTo("myproject-review"))
                .body("mirroredRepositories.size()", equalTo(2))
                .body("mirroredRepositories[0].id", equalTo(1))
                .body("mirroredRepositories[0].upstreamOrganisation", equalTo("up-orgA"))
                .body("mirroredRepositories[0].upstreamRepository", equalTo("up-repoA"))
                .body("mirroredRepositories[1].id", equalTo(2))
                .body("mirroredRepositories[1].upstreamOrganisation", equalTo("up-orgB"))
                .body("mirroredRepositories[1].upstreamRepository", equalTo("up-repoB"))
        ;

    }
    @Test
    public void testUpdateOrganisation() {
        getBaseRequest()
                .body(toJson(new Organisation("Overdone", "overlord")))
                .when().put("/api/config/organisations/2")
                .then()
                .statusCode(200)
                .body("id", equalTo(2))
                .body("name", equalTo("Overdone"))
                .body("toolPrRepo", equalTo("overlord"));

        getBaseRequest()
                .body(Json.createObjectBuilder()
                        .add("name", "Overbård")
                        .add("toolPrRepo", "overbaard-review")
                        .build().toString())
                .when().put("/api/config/organisations/2")
                .then()
                .statusCode(200);
    }

    RequestSpecification getBaseRequest() {
        return given()
                .header(new Header("Authorization", "dummy"))
                .contentType(ContentType.JSON);
    }

    @Test
    public void testAddUpdateDeleteMirroredRepositoryToExisting() {
        getBaseRequest()
                .body(toJson(new MirroredRepository("tmpOrg", "tmpRepo")))
                .when().post("/api/config/organisations/1/repositories")
                .then()
                .statusCode(201);

        int newRepoId = getBaseRequest()
                .queryParam("detail", true)
                .when().get("/api/config/organisations/1")
                .then()
                .statusCode(200)
                .body("mirroredRepositories.size()", equalTo(3))
                .body("mirroredRepositories[0].id", equalTo(1))
                .body("mirroredRepositories[0].upstreamOrganisation", equalTo("up-orgA"))
                .body("mirroredRepositories[0].upstreamRepository", equalTo("up-repoA"))
                .body("mirroredRepositories[1].id", equalTo(2))
                .body("mirroredRepositories[1].upstreamOrganisation", equalTo("up-orgB"))
                .body("mirroredRepositories[1].upstreamRepository", equalTo("up-repoB"))
                .body("mirroredRepositories[2].id", greaterThan(2))
                .body("mirroredRepositories[2].upstreamOrganisation", equalTo("tmpOrg"))
                .body("mirroredRepositories[2].upstreamRepository", equalTo("tmpRepo"))
                .extract().body().path("mirroredRepositories[2].id");

        getBaseRequest()
                .body(toJson(new MirroredRepository("TEMP-ORG", "TEMP-REPO")))
                .when().put("/api/config/organisations/1/repositories/" + newRepoId)
                .then()
                .statusCode(200);

        getBaseRequest()
                .queryParam("detail", true)
                .when().get("/api/config/organisations/1")
                .then()
                .statusCode(200)
                .body("mirroredRepositories.size()", equalTo(3))
                .body("mirroredRepositories[0].id", equalTo(1))
                .body("mirroredRepositories[0].upstreamOrganisation", equalTo("up-orgA"))
                .body("mirroredRepositories[0].upstreamRepository", equalTo("up-repoA"))
                .body("mirroredRepositories[1].id", equalTo(2))
                .body("mirroredRepositories[1].upstreamOrganisation", equalTo("up-orgB"))
                .body("mirroredRepositories[1].upstreamRepository", equalTo("up-repoB"))
                .body("mirroredRepositories[2].id", greaterThan(2))
                .body("mirroredRepositories[2].upstreamOrganisation", equalTo("TEMP-ORG"))
                .body("mirroredRepositories[2].upstreamRepository", equalTo("TEMP-REPO"));

        getBaseRequest()
                .when().delete("/api/config/organisations/1/repositories/" + newRepoId)
                .then()
                .statusCode(204);

        getBaseRequest()
                .queryParam("detail", true)
                .when().get("/api/config/organisations/1")
                .then()
                .statusCode(200)
                .body("mirroredRepositories.size()", equalTo(2))
                .body("mirroredRepositories[0].id", equalTo(1))
                .body("mirroredRepositories[0].upstreamOrganisation", equalTo("up-orgA"))
                .body("mirroredRepositories[0].upstreamRepository", equalTo("up-repoA"))
                .body("mirroredRepositories[1].id", equalTo(2))
                .body("mirroredRepositories[1].upstreamOrganisation", equalTo("up-orgB"))
                .body("mirroredRepositories[1].upstreamRepository", equalTo("up-repoB"));
    }

    @Test
    public void testAddUpdateDeleteMirroredRepositoryToEmpty() {
        getBaseRequest()
                .body(toJson(new MirroredRepository("tmpOrg", "tmpRepo")))
                .when().post("/api/config/organisations/2/repositories")
                .then()
                .statusCode(201);

        int newRepoId = getBaseRequest()
                .queryParam("detail", true)
                .when().get("/api/config/organisations/2")
                .then()
                .statusCode(200)
                .log().body()
                .body("mirroredRepositories.size()", equalTo(1))
                .body("mirroredRepositories[0].id", greaterThan(0))
                .body("mirroredRepositories[0].upstreamOrganisation", equalTo("tmpOrg"))
                .body("mirroredRepositories[0].upstreamRepository", equalTo("tmpRepo"))
                .extract().body().path("mirroredRepositories[0].id");

        getBaseRequest()
                .body(toJson(new MirroredRepository("TEMP-ORG", "TEMP-REPO")))
                .when().put("/api/config/organisations/1/repositories/" + newRepoId)
                .then()
                .statusCode(200);

        getBaseRequest()
                .queryParam("detail", true)
                .when().get("/api/config/organisations/2")
                .then()
                .statusCode(200)
                .body("mirroredRepositories.size()", equalTo(1))
                .body("mirroredRepositories[0].id", greaterThan(2))
                .body("mirroredRepositories[0].upstreamOrganisation", equalTo("TEMP-ORG"))
                .body("mirroredRepositories[0].upstreamRepository", equalTo("TEMP-REPO"));

        getBaseRequest()
                .when().delete("/api/config/organisations/2/repositories/" + newRepoId)
                .then()
                .statusCode(204);

        getBaseRequest()
                .queryParam("detail", true)
                .when().get("/api/config/organisations/2")
                .then()
                .statusCode(200)
                .body("mirroredRepositories.size()", equalTo(0));
    }

    private String toJson(Object o) {
        return JsonbBuilder.create(new JsonbConfig()).toJson(o);
    }
}
