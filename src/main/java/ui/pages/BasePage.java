package ui.pages;

import api.models.roles.AdminLogin;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;

import static api.requests.specs.RequestSpecs.fetchSessionCookie;
import static api.requests.specs.RequestSpecs.setCookieInBrowser;

@SuppressWarnings({"rawtypes", "unchecked"})
public abstract class BasePage<T extends BasePage> {

    public abstract String url();

    public abstract <T extends BasePage> T checkItIsCorrectPage();

    public T open() {
        return Selenide.open(url(), (Class<T>) this.getClass());
    }

    public T open(Object... params) {
        return (T) Selenide.open(String.format(url(), params), (Class<T>) this.getClass());
    }

    public <T extends BasePage> T getPage(Class<T> pageClass) {
        return Selenide.page(pageClass);
    }

    public static void authAsUser(String username, String password) {
        Selenide.open("/");

        io.restassured.http.Cookie sessionCookie = fetchSessionCookie(username, password);
        setCookieInBrowser(sessionCookie);
    }

    public static void authAsUser(AdminLogin admin) {
        authAsUser(admin.getUsername(), admin.getPassword());
    }
}
