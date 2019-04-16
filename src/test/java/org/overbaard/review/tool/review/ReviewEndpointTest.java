package org.overbaard.review.tool.review;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.nullValue;

import javax.json.bind.JsonbBuilder;
import javax.json.bind.JsonbConfig;

import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import io.restassured.http.Header;
import io.restassured.specification.RequestSpecification;

/**
 * @author <a href="mailto:kabir.khan@jboss.com">Kabir Khan</a>
 */
@QuarkusTest
public class ReviewEndpointTest {

    private static final String LONG_STRING = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Pellentesque ac massa eget metus ornare sodales ac eget ligula. Vivamus finibus lectus consectetur urna imperdiet, vel rutrum magna scelerisque. In scelerisque vulputate mi vel ornare. Fusce eu arcu cursus diam egestas lacinia. Suspendisse ac arcu et nulla efficitur sollicitudin eu vel nulla. Integer sit amet fringilla quam. Duis fringilla quam quis accumsan aliquet. Mauris est tellus, suscipit et aliquam ut, varius sit amet elit. Nullam mattis felis a risus efficitur, iaculis varius est euismod. Cras egestas sed magna ut sagittis. Integer consectetur, quam eu pretium convallis, mauris eros sodales nisl, quis finibus dui massa et diam. Duis et consequat est, at auctor eros. Curabitur euismod vulputate varius. Donec tristique, mi in malesuada interdum, mi eros efficitur mauris, ut lobortis lorem magna eget tortor. Phasellus commodo ante id ex efficitur, sit amet tempus diam pellentesque. Ut molestie viverra mollis.\n" +
            "\n" +
            "Nunc eget tincidunt eros. Proin aliquet neque erat, et ultrices libero faucibus id. Duis sit amet convallis felis, at molestie ante. In nisl libero, tempor non turpis suscipit, aliquet vehicula neque. Fusce tristique aliquam turpis, sed lobortis sapien commodo et. Praesent feugiat nisi at libero vulputate facilisis. Sed ipsum massa, sagittis et quam vitae, mattis mattis sem. Maecenas diam enim, dignissim vitae turpis eu, posuere rutrum dui.\n" +
            "\n" +
            "Vivamus ut dignissim purus. Etiam rutrum ut sapien eu accumsan. Vestibulum eu mi in nunc imperdiet tempus in sed risus. Etiam gravida erat vel orci lacinia, sed condimentum tellus vehicula. In risus nisl, gravida non purus at, porta volutpat nisi. Phasellus egestas faucibus pharetra. Curabitur eget elit dapibus, dictum sapien vel, fermentum ipsum. Nunc et nisl quis dui consectetur euismod in at elit. Sed rhoncus ipsum eget est rhoncus iaculis. Vivamus quis purus nec est dapibus rutrum.\n" +
            "\n" +
            "Donec pharetra augue eros. Aliquam molestie at nibh et ultrices. Donec non nisi nisi. Aliquam erat volutpat. Aliquam sollicitudin mollis luctus. Nam fringilla scelerisque risus, ac iaculis nibh accumsan sed. Curabitur ligula massa, elementum id justo vel, ornare suscipit elit. Cras a laoreet velit.\n" +
            "\n" +
            "Quisque tellus dui, pellentesque in iaculis nec, consequat elementum metus. Donec non ex interdum, porttitor lectus sit amet, eleifend orci. Duis vitae nunc placerat, tristique neque vel, tempor sapien. Aenean euismod dictum mi, iaculis rutrum diam. Vivamus imperdiet magna sit amet ultrices pulvinar. Proin ac auctor ligula, quis efficitur arcu. Phasellus et nibh facilisis, pellentesque nibh ac, imperdiet mi. Nulla condimentum laoreet elementum. Aliquam suscipit, leo vel tristique tempor, mi mi pharetra ipsum, at ornare dui diam ac ipsum. Cras nec lorem nunc. Fusce fringilla sed augue a euismod. Integer id posuere dolor, at finibus tortor.";

    @Test
    public void testAllReviews() {
        getBaseRequest()
                .when().get("/api/review")
                .then()
                .statusCode(200)
                .log().body()
                .body("size()", equalTo(2))
                .body("[0].id", equalTo(1))
                .body("[0].title", equalTo("First one"))
                .body("[0].description", nullValue())
                .body("[0].issueTrackerLink", equalTo("https://example.com/100"))
                .body("[0].owner.id", equalTo(3))
                .body("[0].owner.login", equalTo("non_admin"))
                .body("[1].id", equalTo(2))
                .body("[1].title", equalTo("Second one"))
                .body("[1].description", nullValue())
                .body("[1].issueTrackerLink", equalTo("https://example.com/101"))
                .body("[1].owner.id", equalTo(2))
                .body("[1].owner.login", equalTo("test_user"));
    }

    @Test
    public void testOrganisationReviews() {
        getBaseRequest()
                .when().get("/api/review/organisation/1")
                .then()
                .statusCode(200)
                .log().body()
                .body("size()", equalTo(1))
                .body("[0].id", equalTo(1))
                .body("[0].title", equalTo("First one"))
                .body("[0].description", nullValue())
                .body("[0].issueTrackerLink", equalTo("https://example.com/100"))
                .body("[0].owner.id", equalTo(3))
                .body("[0].owner.login", equalTo("non_admin"));

        getBaseRequest()
                .when().get("/api/review/organisation/2")
                .then()
                .statusCode(200)
                .body("size()", equalTo(1))
                .body("[0].id", equalTo(2))
                .body("[0].title", equalTo("Second one"))
                .body("[0].description", nullValue())
                .body("[0].issueTrackerLink", equalTo("https://example.com/101"))
                .body("[0].owner.id", equalTo(2))
                .body("[0].owner.login", equalTo("test_user"));
    }

    @Test
    public void testGetReviewDetail() {
        getBaseRequest()
                .when().get("/api/review/2")
                .then()
                .statusCode(200)
                .log().body()
                .body("id", equalTo(2))
                .body("title", equalTo("Second one"))
                .body("description", equalTo("Blabla"))
                .body("issueTrackerLink", equalTo("https://example.com/101"))
                .body("owner.id", equalTo(2))
                .body("owner.login", equalTo("test_user"))
                .body("organisation.id", equalTo(2));
    }

    @Test
    public void testCreateUpdateDeleteReviewRequest() {
        // Use Integer here to avoid CCE although the entity uses Long
        Integer id = getBaseRequest()
                .body(toJson(new ReviewRequest("New one", "https://example.com/999", LONG_STRING)))
                .when().post("/api/review/organisation/2")
                .then()
                .statusCode(201)
                .log().body()
                .body("id", greaterThan(2))
                .body("title", equalTo("New one"))
                .body("description", equalTo(LONG_STRING))
                .body("issueTrackerLink", equalTo("https://example.com/999"))
                .body("owner.id", equalTo(2))
                .body("owner.login", equalTo("test_user"))
                .body("organisation.id", equalTo(2))
                .extract().body().path("id");

        getBaseRequest()
                .body(toJson(new ReviewRequest("Updated one", "https://example.com/666", "a" + LONG_STRING)))
                .when().put("/api/review/" + id)
                .then()
                .statusCode(204);

        getBaseRequest()
                .when().get("/api/review/" + id)
                .then()
                .statusCode(200)
                .body("id", equalTo(id))
                .body("title", equalTo("Updated one"))
                .body("description", equalTo("a" + LONG_STRING))
                .body("issueTrackerLink", equalTo("https://example.com/666"))
                .body("owner.id", equalTo(2))
                .body("owner.login", equalTo("test_user"))
                .body("organisation.id", equalTo(2));

        getBaseRequest()
                .when().get("/api/review/organisation/1")
                .then()
                .statusCode(200)
                .log().body()
                .body("size()", equalTo(1))
                .body("[0].id", equalTo(1));

        getBaseRequest()
                .when().get("/api/review/organisation/2")
                .then()
                .statusCode(200)
                .body("size()", equalTo(2))
                .body("[0].id", equalTo(2))
                .body("[1].id", equalTo(id));

        getBaseRequest()
                .when().delete("/api/review/" + id)
                .then()
                .statusCode(204);

        getBaseRequest()
                .when().get("/api/review/organisation/2")
                .then()
                .statusCode(200)
                .body("size()", equalTo(1))
                .body("[0].id", equalTo(2));
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
