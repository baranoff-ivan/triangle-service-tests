package triangle.service.tests;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import triangle.service.tests.payloads.CreateTriangleRequest;
import triangle.service.tests.payloads.Result;
import triangle.service.tests.payloads.Triangle;

import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class TriangleAreaTest extends TriangleServiceTestBase {

    private Triangle triangle;

    @DataProvider(name = "areas")
    public Object[][] createTriangleAreasData() {
        return new Object[][] {
                // Expected area is calculated according to Heron's formula
                { "3;4;5", 6.0 },
                { "0.5;0.75;1.0", 0.18154609435347266},
                { "1000000.001;1000000;1000000", 433012702180.8946},
                { "1;1;0", 0},
                { "1;1;2", 0},
                { "0;0;0", 0}
        };
    }

    @Test(dataProvider = "areas")
    public void getTriangleAreaTest(String input, double expectedArea) {
        triangle = createTriangle(new CreateTriangleRequest(input));

        Result result = given().
                spec(createDefaultRequestSpec()).
        when().
                pathParam("triangleId", triangle.getId()).
                get("/triangle/{triangleId}/area").
        then().
                log().all().
                statusCode(200).
                extract().as(Result.class);

        assertThat(result.getResult(), closeTo(expectedArea, 1E-6));
    }

    @Test
    public void unauthorizedRequestTest() {
        triangle = createTriangle(new CreateTriangleRequest("3;4;5"));

        given().
                spec(createUnauthorizedRequestSpec()).
        when().
                pathParam("triangleId", triangle.getId()).
                get("/triangle/{triangleId}/area").
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
                get("/triangle/{triangleId}/area").
        then().
                log().all().
                statusCode(404).
                body("error", equalTo("Not Found"));
    }

    @AfterMethod
    public void tearDown() {
        if (triangle != null) {
            deleteTriangle(triangle.getId());
        }
    }

}
