package triangle.service.tests;

import io.restassured.mapper.TypeRef;
import org.hamcrest.Matchers;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;
import triangle.service.tests.payloads.CreateTriangleRequest;
import triangle.service.tests.payloads.Triangle;

import java.util.List;

import static io.restassured.RestAssured.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class GetAllTrianglesTest extends TriangleServiceTestBase {

    private Triangle triangle;

    @Test
    public void getAllTrianglesTest() {
        List<Triangle> triangles = given().
                spec(createDefaultRequestSpec()).
        when().
                get("/triangle/all").
        then().
                log().all().
                statusCode(200).
                extract().as(new TypeRef<List<Triangle>>() {});

        triangle = createTriangle(new CreateTriangleRequest("3;4;5"));

        List<Triangle> trianglesAfterAddition = given().
                spec(createDefaultRequestSpec()).
        when().
                get("/triangle/all").
        then().
                log().all().
                statusCode(200).
                extract().as(new TypeRef<List<Triangle>>() {});

        assertThat(triangles, not(hasItem(hasProperty("id", equalTo(triangle.getId())))));
        assertThat(trianglesAfterAddition, hasItem(hasProperty("id", equalTo(triangle.getId()))));
    }

    @Test
    public void unauthorizedRequestTest() {
        given().
                spec(createUnauthorizedRequestSpec()).
        when().
                get("/triangle/all").
        then().
                log().all().
                statusCode(401).
                body("error", equalTo("Unauthorized"));
    }

    @AfterMethod
    public void tearDown() {
        if (triangle != null) {
            deleteTriangle(triangle.getId());
        }
    }
}
