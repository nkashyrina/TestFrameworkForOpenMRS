package api.requests.skeleton.requesters;

import api.configs.Config;
import api.requests.Endpoint;
import api.requests.HttpRequest;
import api.requests.skeleton.interfaces.SessionEndpointInterface;
import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;

import static io.restassured.RestAssured.given;

public class AuthRequester extends HttpRequest implements SessionEndpointInterface {
    private static final String API_VERSION = Config.getProperty(Config.API_VERSION_CONST);

    public AuthRequester(RequestSpecification requestSpecification, Endpoint endpoint, ResponseSpecification... responseSpecifications) {
        super(requestSpecification, endpoint, responseSpecifications);
    }

    @Override
    public ValidatableResponse get(String username, String password) {
        return given()
                .auth().preemptive().basic(username, password)
                .spec(requestSpecification)
                .when()
                .get(Endpoint.SESSION.getUrl())
                .then()
                .assertThat()
                .spec(responseSpecifications);
    }
}
