package api.requests.skeleton.requesters;

import api.models.BaseModel;
import api.requests.Endpoint;
import api.requests.HttpRequest;
import api.requests.skeleton.interfaces.CrudEndpointInterface;
import api.utils.JsonUtils;
import common.storages.EntityStorage;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;

import java.util.List;
import java.util.Map;

public class ValidatedCrudRequester<T extends BaseModel> extends HttpRequest implements CrudEndpointInterface {
    private final CrudRequester crudRequester;

    public ValidatedCrudRequester(RequestSpecification requestSpecification, Endpoint endpoint, ResponseSpecification... responseSpecifications) {
        super(requestSpecification, endpoint, responseSpecifications);
        this.crudRequester = new CrudRequester(requestSpecification, endpoint, responseSpecifications);
    }

    @Override
    public T get(String uuid, Class<?> clazz) {
        BaseModel response = crudRequester.get(uuid, clazz).extract().as(endpoint.getResponseModel());

        if (!endpoint.getResponseModel().isInstance(response)) {
            throw new IllegalStateException("Unexpected response type: " + response.getClass());
        }

        return (T) response;
    }

    @Override
    public List<T> getAll(String uuid, Class<?> clazz) {
        String jsonString = crudRequester.get(uuid, clazz)
                .extract()
                .response()
                .asString();

        return JsonUtils.extractResultsList(jsonString, (Class<T>) clazz);
    }

    @Override
    public List<T> getAll(Map<String, Object> params, Class<?> clazz) {
        String jsonString = crudRequester.getAll(params, clazz)
                .extract()
                .response()
                .asString();

        return JsonUtils.extractResultsList(jsonString, (Class<T>) clazz);
    }

    @Override
    public T post(BaseModel model) {
        BaseModel response = crudRequester.post(model).extract().as(endpoint.getResponseModel());

        if (!endpoint.getResponseModel().isInstance(response)) {
            throw new IllegalStateException("Unexpected response type: " + response.getClass());
        }

        EntityStorage.add(response);

        return (T) response;
    }

    @Override
    public T post(BaseModel model, String uuid) {
        BaseModel response = crudRequester.post(model, uuid).extract().as(endpoint.getResponseModel());

        if (!endpoint.getResponseModel().isInstance(response)) {
            throw new IllegalStateException("Unexpected response type: " + response.getClass());
        }

        EntityStorage.add(response);

        return (T) response;
    }

    @Override
    public Void delete(String uuid) {
        return null;
    }

    @Override
    public Void delete(String uuid, Boolean purge) {
        return null;
    }
}
