package api;

import api.configs.Config;
import api.models.RetrieveSessionResponse;
import api.models.roles.AdminLogin;
import api.requests.Endpoint;
import api.requests.skeleton.requesters.ValidatedAuthRequester;
import api.requests.specs.RequestSpecs;
import api.requests.specs.ResponseSpecs;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class AuthenticationTest extends BaseTest {

    @Test
    public void adminCanBeAuthenticatedTest() {
        final String adminRole = "Super User";
        AdminLogin admin = AdminLogin.getAdmin();

        RetrieveSessionResponse session = new ValidatedAuthRequester(
                RequestSpecs.unauthSpec(),
                Endpoint.SESSION,
                ResponseSpecs.requestReturnsOK(),
                ResponseSpecs.requestReturnsSetCookieHeader())
                .get(admin.getUsername(), admin.getPassword());

        assertThat(session.isAuthenticated()).isTrue();
        assertThat(session.getUser().getDisplay()).isEqualTo(admin.getUsername());
        assertThat(session.getUser().getSystemId()).isEqualTo(admin.getUsername());
        assertThat(session.getUser().getPerson().getDisplay()).isEqualTo(adminRole);
    }
}
