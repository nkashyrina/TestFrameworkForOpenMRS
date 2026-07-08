package api;

import api.constants.Constants;
import api.models.IdentifierResponse;
import api.models.IdentifierSource;
import api.requests.Endpoint;
import api.requests.skeleton.requesters.CrudRequester;
import api.requests.skeleton.requesters.ValidatedCrudRequester;
import api.requests.specs.RequestSpecs;
import api.requests.specs.ResponseSpecs;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class GenerateIdentifierTest extends BaseTest {

    @Test
    public void newIdentifierCanBeGeneratedTest() {
        IdentifierSource sourceResponse = new ValidatedCrudRequester<IdentifierSource>(
                RequestSpecs.adminSpec(),
                Endpoint.IDENTIFIER_SOURCE,
                ResponseSpecs.requestReturnsOK())
                .getAll(new CrudRequester.QueryBuilder().vEqualsFull().build(),
                        IdentifierSource.class).getFirst();

        String uuidOfIdentifierSource = sourceResponse.getUuid();
        String uuidOfIdentifierType = sourceResponse.getIdentifierType().getUuid();

        String identifier = new ValidatedCrudRequester<IdentifierResponse>(
                RequestSpecs.adminSpec(),
                Endpoint.IDENTIFIER,
                ResponseSpecs.requestReturnsCreated()
        ).post(null, uuidOfIdentifierSource).getIdentifier();

        assertThat(uuidOfIdentifierSource).isEqualTo(Constants.IDENTIFIER_SOURCE_UUID);
        assertThat(uuidOfIdentifierType).isEqualTo(Constants.IDENTIFIER_TYPE_UUID);
        assertThat(identifier).hasSize(7);

    }
}
