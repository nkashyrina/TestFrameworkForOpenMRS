package api.requests.skeleton.requesters;

import api.configs.Config;
import api.requests.HttpRequest;
import api.requests.skeleton.interfaces.DeleteByPathInterface;
import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;

import static io.restassured.RestAssured.given;

public class DeleteRequester extends HttpRequest implements DeleteByPathInterface {
    public DeleteRequester(RequestSpecification requestSpecification, ResponseSpecification... responseSpecifications) {
        super(requestSpecification, null, responseSpecifications);
    }

    @Override
    public ValidatableResponse delete(String path) {
        return given()
                .spec(requestSpecification)
                .when()
                .delete(path)
                .then()
                .spec(responseSpecifications);
    }
}
