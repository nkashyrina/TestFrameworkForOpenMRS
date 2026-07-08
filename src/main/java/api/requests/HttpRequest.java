package api.requests;

import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;

public abstract class HttpRequest {
    protected RequestSpecification requestSpecification;
    protected Endpoint endpoint;
    protected ResponseSpecification responseSpecifications;

    public HttpRequest(RequestSpecification requestSpecification, Endpoint endpoint, ResponseSpecification... responseSpecifications) {
        this.requestSpecification = requestSpecification;
        this.endpoint = endpoint;
        this.responseSpecifications = combine(responseSpecifications);
    }

    private ResponseSpecification combine(ResponseSpecification... specs) {
        ResponseSpecBuilder builder = new ResponseSpecBuilder();
        for (ResponseSpecification spec : specs) {
            builder.addResponseSpecification(spec);
        }
        return builder.build();
    }
}
