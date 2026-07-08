package api.requests.specs;

import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.specification.ResponseSpecification;
import org.apache.http.HttpStatus;

import static org.hamcrest.Matchers.*;

public final class ResponseSpecs {
    public final static String GIVEN_NAME_FIELD = "givenName";
    public final static String YOU_MUST_DEFINE_THE_GIVEN_NAME_ERROR = "You must define the Given Name";
    public final static String OBJECT_WITH_GIVEN_UUID_DOES_NOT_EXIST = "Object with given uuid doesn't exist [null]";

    private ResponseSpecs() {
    }

    private static ResponseSpecBuilder defaultResponseBuilder() {
        return new ResponseSpecBuilder();
    }

    public static ResponseSpecification requestReturnsOK() {
        return defaultResponseBuilder()
                .expectStatusCode(HttpStatus.SC_OK)
                .build();
    }

    public static ResponseSpecification requestReturnsCreated() {
        return defaultResponseBuilder()
                .expectStatusCode(HttpStatus.SC_CREATED)
                .build();
    }

    public static ResponseSpecification requestReturnsNoContent() {
        return defaultResponseBuilder()
                .expectStatusCode(HttpStatus.SC_NO_CONTENT)
                .build();
    }

    public static ResponseSpecification requestReturnsSetCookieHeader() {
        return defaultResponseBuilder()
                .expectHeader("Set-Cookie", containsString("JSESSIONID"))
                .build();
    }

    public static ResponseSpecification requestReturnsBadRequest() {
        return defaultResponseBuilder()
                .expectStatusCode(HttpStatus.SC_BAD_REQUEST)
                .build();
    }

    public static ResponseSpecification requestReturnBadRequestForIncorrectName(String fieldName, String errorValue) {
        return defaultResponseBuilder()
                .expectStatusCode(HttpStatus.SC_BAD_REQUEST)
                .expectBody("error.fieldErrors",
                        hasEntry(
                                equalTo("names[0]." + fieldName),
                                hasItem(hasEntry("message", errorValue))
                        ))
                .build();
    }

    public static ResponseSpecification requestReturnsNotFoundWithMessage(String expectedMessage) {
        return new ResponseSpecBuilder()
                .expectStatusCode(HttpStatus.SC_NOT_FOUND)
                .expectBody("error.message", equalTo(expectedMessage))
                .build();
    }
}
