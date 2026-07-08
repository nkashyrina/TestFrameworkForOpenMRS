package ui.pages;

import api.models.CreatePatientResponse;
import api.models.ui.ActionableNotification;
import api.models.ui.GenderUi;
import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.WebDriverRunner;
import common.storages.EntityStorage;
import common.utils.DateUtils;
import org.openqa.selenium.By;
import ui.components.AddressComponent;
import ui.components.ContactDetailsComponent;

import java.util.Arrays;

import static com.codeborne.selenide.Selenide.$;

public class PatientSummaryPage extends BasePage<PatientSummaryPage> {
    public static final String OPEN_MRS_ID_TEXT = "OpenMRS ID: ";

    private AddressComponent addressComponent;
    private ContactDetailsComponent contactDetailsComponent;

    private final SelenideElement patientName = $("span._3QvC113UMQvMOBhW\\+z79\\+Q\\=\\=");
    private final SelenideElement openMrsId = $(By.xpath("span.cds--tag__label"));
    private final SelenideElement openMrsIdNumber = $("span._7O7gKi8oSk8dU4N7z9kfrQ\\=\\=");
    private final SelenideElement openMrsIdText = $(By.xpath("//span[text()='OpenMRS ID']"));
    private final SelenideElement genderText = $("div.qIV9qXAs11cCtcKxvIip4w\\=\\=>span");
    private final SelenideElement genderIcon = $("div.qIV9qXAs11cCtcKxvIip4w\\=\\= > svg.kkUJqagTWOXdl47D96eILQ\\=\\= > use");
    private final SelenideElement ageText = $("div.m8jQX0Xu7TIqdLMfGF5vMw\\=\\= > span:nth-child(1)");
    private final SelenideElement birthDateText = $("div.m8jQX0Xu7TIqdLMfGF5vMw\\=\\= > span:nth-child(3)");
    private final SelenideElement showMoreButton = $(By.xpath("//button[text()='Show more']"));
    private final SelenideElement vitalsHistoryLink = $("a[href*='Vitals ']");

    @Override
    public String url() {
        return "/patient/%s/chart/Patient%%20Summary";
    }

    @Override
    public PatientSummaryPage checkItIsCorrectPage() {
        vitalsHistoryLink.shouldBe(Condition.visible);
        openMrsIdNumber.shouldBe(Condition.visible);

        return this;
    }

    public PatientSummaryPage checkPatientNameIsEqualTo(String name) {
        patientName.shouldBe(Condition.visible);
        patientName.shouldHave(Condition.exactText(name));

        return this;
    }

    public PatientSummaryPage checkBirthDateIsEqualTo(String birthdate) {
        final String expectedDateFormat = DateUtils.convertDdMmYyyyToDdMmmYyyy(birthdate);
        birthDateText.shouldBe(Condition.visible);
        birthDateText.shouldHave(Condition.exactText(expectedDateFormat));

        return this;
    }

    public PatientSummaryPage checkEstimatedBirthDateIsEqualTo(Integer age) {
        final String expectedDateFormat = DateUtils.convertAgeToMmmYyyy(age);
        birthDateText.shouldBe(Condition.visible);
        birthDateText.shouldHave(Condition.exactText(expectedDateFormat));

        return this;
    }

    public PatientSummaryPage checkIdPrefixIsEqualTo(String idPrefix) {
        openMrsIdText.shouldBe(Condition.visible);
        openMrsIdText.shouldHave(Condition.exactText(idPrefix));

        return this;
    }

    public PatientSummaryPage checkGenderIsEqualTo(String gender) {
        genderText.shouldBe(Condition.visible);
        genderText.shouldHave(Condition.exactText(gender));

        return this;
    }

    public PatientSummaryPage checkGenderIconIsCorrect() {
        final String gender = genderText.getText();
        final String expectedIconValue = Arrays.stream(GenderUi.values())
                .filter(it -> it.getGender().equals(gender)).findFirst().get().getIconId();

        genderIcon.shouldHave(Condition.attribute("href", expectedIconValue));

        return this;
    }

    public PatientSummaryPage clickShowMoreButton() {
        showMoreButton.shouldBe(Condition.visible);
        showMoreButton.click();

        return this;
    }

    public AddressComponent getAddressComponent() {
        return new AddressComponent($(By.xpath("//p[text()='Address']/../ul")));
    }

    public ContactDetailsComponent getContactDetailsComponent() {
        return new ContactDetailsComponent($(By.xpath("//p[text()='Contact Details']/../ul")));
    }

    public String getPatientUuid() {
        String url = WebDriverRunner.url();
        return url.replaceAll(".*/patient/([^/]+)/.*", "$1");
    }

    public String getOpenMrsIdTextInApiFormat() {
        String text = openMrsIdText.shouldBe(Condition.visible).getText();
        text = text.replace(":", "");
        String id = openMrsIdNumber.shouldBe(Condition.visible).getText();

        return String.format("%s = %s", text, id);
    }

    public PatientSummaryPage verifySuccessNotification() {
        $(".cds--actionable-notification--toast").shouldBe(Condition.visible);

        $(".cds--actionable-notification__title")
                .shouldHave(Condition.exactText(ActionableNotification.NEW_PATIENT_CREATED.getNotificationTitle()));

        $(".cds--actionable-notification__subtitle")
                .shouldHave(Condition.exactText(ActionableNotification.NEW_PATIENT_CREATED.getNotificationSubTitle()));

        return this;
    }

    public PatientSummaryPage addPatientToEntityStorage(CreatePatientResponse patientResponse) {
        EntityStorage.add(patientResponse);

        return this;
    }
}
