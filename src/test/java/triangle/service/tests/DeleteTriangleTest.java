package triangle.service.tests;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;
import triangle.service.tests.payloads.CreateTriangleRequest;
import triangle.service.tests.payloads.Triangle;

import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

public class DeleteTriangleTest extends TriangleServiceTestBase {

    private Triangle triangle;

    @Test
    public void deleteTriangleTest() {
        triangle = createTriangle(new CreateTriangleRequest("3;4;5"));

        given().
                spec(createDefaultRequestSpec()).
        when().
                pathParam("triangleId", triangle.getId()).
                delete("/triangle/{triangleId}").
        then().
                log().all().
                statusCode(200);

        // Ensure triangle doesn't exist anymore
        given().
                spec(createDefaultRequestSpec()).
        when().
                pathParam("triangleId", triangle.getId()).
                get("/triangle/{triangleId}").
        then().
                log().all().
                statusCode(404);
    }

    @Test
    public void unauthorizedRequestTest() {
        triangle = createTriangle(new CreateTriangleRequest("3;4;5"));

        given().
                spec(createUnauthorizedRequestSpec()).
        when().
                pathParam("triangleId", triangle.getId()).
                delete("/triangle/{triangleId}").
        then().
                log().all().
                statusCode(401).
                body("error", equalTo("Unauthorized"));
    }

    @Test
    public void notFoundTest() {
        given().
                spec(createDefaultRequestSpec()).
        when().
                pathParam("triangleId", UUID.randomUUID().toString()).
                delete("/triangle/{triangleId}").
        then().
                log().all().
                statusCode(200); // If not found - nothing to delete and it's OK
    }

    @AfterMethod
    public void tearDown() {
        if (triangle != null) {
            deleteTriangle(triangle.getId());
        }
    }
}
