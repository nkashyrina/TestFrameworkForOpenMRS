package api.requests.skeleton.requesters;

import api.models.RetrieveSessionResponse;
import api.requests.Endpoint;
import api.requests.HttpRequest;
import api.requests.skeleton.interfaces.SessionEndpointInterface;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;

public class ValidatedAuthRequester extends HttpRequest implements SessionEndpointInterface {
    private AuthRequester authRequester;

    public ValidatedAuthRequester(RequestSpecification requestSpecification, Endpoint endpoint, ResponseSpecification... responseSpecifications) {
        super(requestSpecification, endpoint, responseSpecifications);
        this.authRequester = new AuthRequester(requestSpecification, endpoint, responseSpecifications);
    }

    @Override
    public RetrieveSessionResponse get(String username, String password) {
        return authRequester.get(username, password).extract().as(RetrieveSessionResponse.class);
    }
}
