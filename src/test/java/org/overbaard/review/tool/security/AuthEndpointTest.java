package org.overbaard.review.tool.security;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;

import javax.json.bind.JsonbBuilder;
import javax.json.bind.JsonbConfig;

import org.junit.jupiter.api.Test;
import org.overbaard.review.tool.mocks.MockGitHubRestClient;
import org.overbaard.review.tool.security.github.GitHubUser;
import org.overbaard.review.tool.util.SimpleJsonValue;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import io.restassured.http.Header;
import io.restassured.specification.RequestSpecification;

/**
 * @author <a href="mailto:kabir.khan@jboss.com">Kabir Khan</a>
 */
@QuarkusTest
public class AuthEndpointTest {

    @Test
    public void testSiteAdmin() {
        getBaseRequest()
                .when().get("/api/auth/siteAdmin")
                .then()
                .statusCode(200)
                .body("size()", equalTo(2))
                .body("[0].id", equalTo(1))
                .body("[0].login", equalTo("kabir"))
                .body("[0].name", equalTo("Kabir Khan"))
                .body("[0].email", equalTo("kkhan@redhat.com"))
                .body("[1].id", equalTo(2))
                .body("[1].login", equalTo("test_user"))
                .body("[1].name", equalTo("Mock Test User"))
                .body("[1].email", equalTo("test_user@example.com"));
    }

    @Test
    public void testSetSiteAdmin() {
        getBaseRequest()
                .when().get("/api/auth/siteAdmin/kabir")
                .then()
                .statusCode(200)
                .body("value", equalTo(true));

        getBaseRequest()
                .body(toJson(new SimpleJsonValue(false)))
                .when().put("/api/auth/siteAdmin/kabir")
                .then()
                .statusCode(204);

        getBaseRequest()
                .when().get("/api/auth/siteAdmin")
                .then()
                .statusCode(200)
                .body("size()", equalTo(1));
        getBaseRequest()
                .when().get("/api/auth/siteAdmin/kabir")
                .then()
                .statusCode(200)
                .body("value", equalTo(false));

        getBaseRequest()
                .body(toJson(new SimpleJsonValue(true)))
                .when().put("/api/auth/siteAdmin/kabir")
                .then()
                .statusCode(204);

        getBaseRequest()
                .when().get("/api/auth/siteAdmin")
                .then()
                .statusCode(200)
                .body("size()", equalTo(2));
        getBaseRequest()
                .when().get("/api/auth/siteAdmin/kabir")
                .then()
                .statusCode(200)
                .body("value", equalTo(true));
    }

    @Test
    public void testSetSiteAdminNotFoundInGitHub() {
        getBaseRequest()
                .body(toJson(new SimpleJsonValue(true)))
                .when().put("/api/auth/siteAdmin/snsoihsmcbsi8eywiubso8yrwoih20372hd")
                .then()
                .statusCode(404);
    }

    @Test
    public void testSetSiteAdminFromGitHub() {
        // Jason Greene ('n1hility') is a valid user on GitHub who is not likely to ever work on this project,
        // and so is unlikely to ever be set up by our import.sql
        MockGitHubRestClient.usersByName.put("n1hility", new GitHubUser(99999, "n1hility", "Jason Greene", "jason@blah.com", "http://example.com/123"));
        try {
            getBaseRequest()
                    .body(toJson(new SimpleJsonValue(true)))
                    .when().put("/api/auth/siteAdmin/n1hility")
                    .then()
                    .statusCode(204);
            try {
                getBaseRequest()
                        .when().get("/api/auth/siteAdmin")
                        .then()
                        .statusCode(200)
                        .body("size()", equalTo(3))
                        // The query sorts by name, so Jason comes first
                        .body("[0].id", greaterThan(3))
                        .body("[0].login", equalTo("n1hility"))
                        .body("[0].name", equalTo("Jason Greene"))
                        .body("[0].email", equalTo("jason@blah.com"))
                        .log().body();

            } finally {
                getBaseRequest()
                        .body(toJson(new SimpleJsonValue(false)))
                        .when().put("/api/auth/siteAdmin/n1hility")
                        .then()
                        .statusCode(204);
            }
        } finally {
            MockGitHubRestClient.usersByName.clear();
        }
    }

    @Test
    public void testGetOrganisationAdmins() {
        getBaseRequest()
                .when().get("/api/auth/organisation/1/admin")
                .then()
                .statusCode(200)
                .log().body()
                .body("size()", equalTo(1))
                .body("[0].id", equalTo(1))
                .body("[0].login", equalTo("kabir"))
                .body("[0].name", equalTo("Kabir Khan"))
                .body("[0].email", equalTo("kkhan@redhat.com"));
    }

    @Test
    public void testAddOrganisationAdmin() {
        getBaseRequest()
                .when().post("/api/auth/organisation/1/admin/non_admin")
                .then()
                .statusCode(204);

        try {
            getBaseRequest()
                    .when().get("/api/auth/organisation/1/admin")
                    .then()
                    .statusCode(200)
                    .log().body()
                    .body("size()", equalTo(2))
                    .body("[0].id", equalTo(1))
                    .body("[0].login", equalTo("kabir"))
                    .body("[0].name", equalTo("Kabir Khan"))
                    .body("[0].email", equalTo("kkhan@redhat.com"))
                    .body("[1].id", equalTo(3))
                    .body("[1].login", equalTo("non_admin"))
                    .body("[1].name", equalTo("Non Admin"))
                    .body("[1].email", equalTo("non_admin@example.com"));
        } finally {
            getBaseRequest()
                    .when().delete("/api/auth/organisation/1/admin/non_admin")
                    .then()
                    .statusCode(204);

            testGetOrganisationAdmins();
        }

    }

    private String toJson(Object o) {
        return JsonbBuilder.create(new JsonbConfig()).toJson(o);
    }

    private RequestSpecification getBaseRequest() {
        return given()
                .header(new Header("Authorization", "dummy"))
                .contentType(ContentType.JSON);
    }

}
