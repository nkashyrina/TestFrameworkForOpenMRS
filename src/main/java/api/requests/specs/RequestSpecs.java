package api.requests.specs;

import api.configs.Config;
import api.requests.Endpoint;
import api.requests.skeleton.requesters.AuthRequester;
import com.codeborne.selenide.WebDriverRunner;
import com.github.viclovsky.swagger.coverage.FileSystemOutputWriter;
import com.github.viclovsky.swagger.coverage.SwaggerCoverageRestAssured;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.openqa.selenium.Cookie;

import java.nio.file.Paths;
import java.util.List;

import static com.github.viclovsky.swagger.coverage.SwaggerCoverageConstants.OUTPUT_DIRECTORY;

public final class RequestSpecs {
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String JSESSION_ID = "JSESSIONID";
    private static final String ADMIN_TOKEN = Config.getProperty(Config.ADMIN_TOKEN_CONST);

    private RequestSpecs() {
    }

    private static RequestSpecBuilder defaultRequestSpecBuilder() {
        return new RequestSpecBuilder()
                .setContentType(ContentType.JSON)
                .setAccept(ContentType.JSON)
                .addFilters(List.of(
                        new RequestLoggingFilter(),
                        new ResponseLoggingFilter(),
                        new AllureRestAssured(),
                        new SwaggerCoverageRestAssured(
                                new FileSystemOutputWriter(Paths.get("target/" + OUTPUT_DIRECTORY)))
                ))
                .setBaseUri(Config.getProperty(Config.API_BASE_URL_CONST) + Config.getProperty(Config.API_VERSION_CONST));
    }

    public static RequestSpecification unauthSpec() {
        return defaultRequestSpecBuilder().build();
    }

    public static RequestSpecification adminSpec() {
        return defaultRequestSpecBuilder()
                .addHeader(AUTHORIZATION_HEADER, "Basic ".concat(ADMIN_TOKEN))
                .build();
    }

    public static RequestSpecification authWithJSessionId(String jSessionId) {
        return defaultRequestSpecBuilder()
                .addCookie(JSESSION_ID, jSessionId)
                .build();
    }

    public static String getBrowserSessionCookieValue() {
        org.openqa.selenium.Cookie seleniumCookie = WebDriverRunner.getWebDriver()
                .manage()
                .getCookieNamed(JSESSION_ID);

        if (seleniumCookie == null) {
            throw new IllegalStateException("No session cookie found in browser");
        }

        return seleniumCookie.getValue();
    }

    public static io.restassured.http.Cookie fetchSessionCookie(String username, String password) {
        return new AuthRequester(
                unauthSpec(),
                Endpoint.SESSION,
                ResponseSpecs.requestReturnsSetCookieHeader())
                .get(username, password)
                .extract()
                .detailedCookie(JSESSION_ID);
    }

    public static void setCookieInBrowser(io.restassured.http.Cookie cookie) {
        Cookie.Builder builder = new Cookie.Builder(cookie.getName(), cookie.getValue());

        if (cookie.getDomain() != null) builder.domain(cookie.getDomain());
        if (cookie.getPath() != null) builder.path(cookie.getPath());
        if (cookie.getExpiryDate() != null) builder.expiresOn(cookie.getExpiryDate());
        builder.isHttpOnly(cookie.isHttpOnly());
        builder.isSecure(cookie.isSecured());

        WebDriverRunner.getWebDriver().manage().addCookie(builder.build());
    }
}
