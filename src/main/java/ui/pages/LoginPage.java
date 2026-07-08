package ui.pages;

import api.models.ui.ErrorMessages;
import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Selectors;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Selenide.$;

public class LoginPage extends BasePage<LoginPage> {
    @Override
    public String url() {
        return "/login";
    }

    @Override
    public LoginPage checkItIsCorrectPage() {
        passwordField.shouldNotBe(Condition.visible);
        usernameField.shouldBe(Condition.visible);
        continueButton.shouldBe(Condition.visible);

        return this;
    }

    private final SelenideElement usernameField = $("#username");
    private final SelenideElement passwordField = $("#password");
    private final SelenideElement continueButton = $(Selectors.byXpath("//button[text()='Continue']"));
    private final SelenideElement logInButton = $(Selectors.byXpath("//button[text()='Log in']"));

    private final SelenideElement errorWrapper = $(".cds--inline-notification__text-wrapper");
    private final SelenideElement errorTitle = $(".cds--inline-notification__title");
    private final SelenideElement errorMessage = $(".cds--inline-notification__subtitle");
    private final SelenideElement errorMessageCloseButton = $(".cds--inline-notification__close-button");

    public LoginPage populateUserNameField(String username) {
        usernameField.shouldBe(Condition.visible);
        usernameField.sendKeys(username);
        usernameField.click();

        return this;
    }

    public LoginPage populatePasswordField(String password) {
        passwordField.shouldBe(Condition.visible);
        passwordField.sendKeys(password);
        passwordField.click();

        return this;
    }

    public LoginPage clickContinueButton() {
        continueButton.click();

        return this;
    }

    public LoginPage clickLogInButton() {
        logInButton.click();

        return this;
    }

    public LoginPage errorMessageInvalidUsernameOrPasswordIsDisplayed(){
        errorWrapper.shouldBe(Condition.visible);
        errorTitle.shouldHave(Condition.exactText(ErrorMessages.ERROR_TITLE_TEXT.getText()));
        errorMessage.shouldHave(Condition.exactText(ErrorMessages.LOGIN_ERROR_MESSAGE.getText()));
        errorMessageCloseButton.click();

        return this;
    }

    public LoginPage clearSession() {
        Selenide.clearBrowserCookies();
        Selenide.clearBrowserLocalStorage();

        return this;
    }

    public LoginPage enterCredentials(String userName, String password) {
        populateUserNameField(userName);
        clickContinueButton();
        populatePasswordField(password);
        clickLogInButton();

        return this;
    }
}
