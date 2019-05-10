package triangle.service.tests;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import triangle.service.tests.payloads.CreateTriangleRequest;
import triangle.service.tests.payloads.Triangle;

import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class GetTriangleTest extends TriangleServiceTestBase {

    private Triangle triangle;

    @DataProvider(name = "triangles")
    public Object[][] createTriangleData() {
        return new Object[][] {
                { new CreateTriangleRequest("3;4;5")},
                { new CreateTriangleRequest("0.5;0.75;0.5")},
                { new CreateTriangleRequest("1000000.001;1000000.002;1000000.003")},
                { new CreateTriangleRequest("1;1;0")}, // c = 0 Is it triangle?
                { new CreateTriangleRequest("1;1;2")}, // a + b = c Is it triangle?
                { new CreateTriangleRequest("0;0;0")} // It is a point. Is it triangle?
        };
    }

    @Test(dataProvider = "triangles")
    public void getTriangleTest(CreateTriangleRequest requestPayload) {
        triangle = createTriangle(requestPayload);

        Triangle getResponsePayload = given().
                spec(createDefaultRequestSpec()).
        when().
                pathParam("triangleId", triangle.getId()).
                get("/triangle/{triangleId}").
        then().
                log().all().
                statusCode(200).
                extract().as(Triangle.class);

        assertThat(getResponsePayload.getId(), equalTo(triangle.getId()));
        assertThat(getResponsePayload.getFirstSide(), equalTo(triangle.getFirstSide()));
        assertThat(getResponsePayload.getSecondSide(), equalTo(triangle.getSecondSide()));
        assertThat(getResponsePayload.getThirdSide(), equalTo(triangle.getThirdSide()));
    }

    @Test
    public void unauthorizedRequestTest() {
        triangle = createTriangle(new CreateTriangleRequest("3;4;5"));

        given().
                spec(createUnauthorizedRequestSpec()).
        when().
                pathParam("triangleId", triangle.getId()).
                get("/triangle/{triangleId}").
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
                get("/triangle/{triangleId}").
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
