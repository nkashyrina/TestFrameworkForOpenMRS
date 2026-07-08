package ui.pages;

import ui.components.Header;

import static com.codeborne.selenide.Selenide.$;

public abstract class AuthBasePage<T extends AuthBasePage> extends BasePage<AuthBasePage> {
    public Header header = new Header($("#omrs-top-nav-app-container"));
}
