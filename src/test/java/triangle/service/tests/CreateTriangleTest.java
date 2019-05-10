package triangle.service.tests;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import triangle.service.tests.payloads.CreateTriangleRequest;
import triangle.service.tests.payloads.Triangle;

import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class CreateTriangleTest extends TriangleServiceTestBase {

    private Triangle triangle;

    @DataProvider(name = "createTrianglesSuccessData")
    public Object[][] createTrianglesSuccessData() {
        return new Object[][] {
                { new CreateTriangleRequest("3;4;5"), 3.0, 4.0, 5.0},
                { new CreateTriangleRequest("0.5;0.75;0.5"), 0.5, 0.75, 0.5},
                { new CreateTriangleRequest("1000000.001;1000000.002;1000000.003"), 1000000.001, 1000000.002, 1000000.003},
                { new CreateTriangleRequest("3,4,5", ","), 3.0, 4.0, 5.0},
                { new CreateTriangleRequest("3:4:5", ":"), 3.0, 4.0, 5.0},
                { new CreateTriangleRequest("1;1;0"), 1.0, 1.0, 0.0}, // c = 0 Is it triangle?
                { new CreateTriangleRequest("1;1;2"), 1.0, 1.0, 2.0}, // a + b = c Is it triangle?
                { new CreateTriangleRequest("0;0;0"), 0.0, 0.0, 0.0} // It is a point. Is it triangle?
        };
    }

    @Test(dataProvider = "createTrianglesSuccessData")
    public void createTriangleSuccessTest(CreateTriangleRequest requestPayload,
                                          double firstSide,
                                          double secondSide,
                                          double thirdSide) {
        triangle = given(createDefaultRequestSpec()).
                body(requestPayload).
        when().
                post("/triangle").
        then().
                log().all().
                statusCode(200).
                extract().as(Triangle.class);

        assertThat(triangle.getId(), notNullValue());
        assertThat(triangle.getFirstSide(), equalTo(firstSide));
        assertThat(triangle.getSecondSide(), equalTo(secondSide));
        assertThat(triangle.getThirdSide(), equalTo(thirdSide));

    }

    @DataProvider(name = "createTrianglesUnprocessibleData")
    public Object[][] createTrianglesUnprocessibleData() {
        return new Object[][] {
                { new CreateTriangleRequest("3;4;8")}, // a + b < c
                { new CreateTriangleRequest("3;4;-5")},
                { new CreateTriangleRequest("1;1;0,5")}, // 0,5 contains ',' instead of '.'
                { new CreateTriangleRequest("3;4;5;6")},
                { new CreateTriangleRequest("3;4;")},
                { new CreateTriangleRequest("3;4;a")},
                { new CreateTriangleRequest("3;4;5", ":")}
        };
    }

    @Test(dataProvider = "createTrianglesUnprocessibleData")
    public void unprocessbleCreateTriangleRequestTest(CreateTriangleRequest requestPayload) {
        given(createDefaultRequestSpec()).
                body(requestPayload).
        when().
                post("/triangle").
        then().
                log().all().
                statusCode(422).
                body("id", is(nullValue()));
    }

    @Test
    public void unauthorizedRequestTest() {
        given().
                spec(createUnauthorizedRequestSpec()).
                body(new CreateTriangleRequest("3;4;5")).
        when().
                post("/triangle").
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
