import com.fasterxml.jackson.databind.util.JSONPObject;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.specification.RequestSpecification;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.*;
import static io.restassured.matcher.RestAssuredMatchers.*;
import static org.hamcrest.Matchers.*;

public class TriangleServiceTest {
    private RequestSpecification requestSpec;

    @BeforeClass // TODO: Check if annotation is selected correctly
    public void createRequestSpecification() {
        requestSpec = new RequestSpecBuilder().
                setBaseUri("http://akrigator.synology.me:8888").
                setContentType("application/json").
                addHeader("X-User", "730a8f30-5895-4380-b001-dd50bee1290d").
                log(LogDetail.ALL).
                build();
    }

    @Test
    public void getAllTrianglesTest() {
        given().
                spec(requestSpec).
        when().
                get("/triangle/all").
        then().
                log().all().
                statusCode(200).
                body(equalTo("[]"));
    }

    @Test
    public void addTriangleTest() {
        given().
                spec(requestSpec).
                body("{ \"input\": \"3;4;5\" }").
        when().
                post("/triangle").
        then().
                log().all().
                statusCode(200).
                body("id", not(empty()));
    }

    @Test
    public void unauthorizedRequestTest() {
        given().
                spec(requestSpec).
                header("X-User", "").
        when().
                get("/triangle/all").
        then().
                log().all().
                statusCode(401).
                body("error", equalTo("Unauthorized"));
    }
}
