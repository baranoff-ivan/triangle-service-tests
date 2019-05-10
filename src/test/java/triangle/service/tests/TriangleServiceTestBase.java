package triangle.service.tests;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.specification.RequestSpecification;
import org.testng.annotations.*;
import triangle.service.tests.payloads.CreateTriangleRequest;
import triangle.service.tests.payloads.Triangle;

import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class TriangleServiceTestBase {



    @BeforeSuite
    public void suiteSetUp() {
        deleteAllTriangles();
    }

    private static RequestSpecBuilder createCommonRequestSpecBuilder() {
        // TODO Move base url and token to settings file
        return new RequestSpecBuilder().
                setBaseUri("http://akrigator.synology.me:8888").
                setContentType("application/json").
                log(LogDetail.ALL);
    }

    protected static RequestSpecification createDefaultRequestSpec() {
        return createCommonRequestSpecBuilder().
                addHeader("X-User", "730a8f30-5895-4380-b001-dd50bee1290d").
                build();
    }

    protected static RequestSpecification createUnauthorizedRequestSpec() {
        return createCommonRequestSpecBuilder().
                build();
    }

    private void deleteAllTriangles() {
        List<String> trianglesIds = given(createDefaultRequestSpec()).
                get("/triangle/all").
                then().
                extract().path("id");
        for (String id : trianglesIds) {
            deleteTriangle(id);
        }
    }

    protected void deleteTriangle(String triangleId) {
        given(createDefaultRequestSpec()).
                pathParam("triangleId", triangleId).
                delete("triangle/{triangleId}");
    }

    protected Triangle createTriangle(CreateTriangleRequest requestPayload) {
        return given(createDefaultRequestSpec()).
                body(requestPayload).
        when().
                post("/triangle").
        then().
                statusCode(200).
                body("id", not(empty())).
                extract().
                as(Triangle.class);
    }
}
