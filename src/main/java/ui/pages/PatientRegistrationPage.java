package ui.pages;

import api.models.CreatePatientResponse;
import api.models.ui.RegisterMandatoryFieldsPatientUi;
import api.models.ui.RegisterPatientUi;
import api.models.ui.RegisterUnknownPatientUi;
import api.requests.steps.AdminSteps;
import com.codeborne.selenide.CollectionCondition;
import com.codeborne.selenide.Condition;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import common.storages.EntityStorage;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

public class PatientRegistrationPage extends BasePage<PatientRegistrationPage> {
    public static String[] uiMandatoryNameFieldsToBeGenerated = new String[]{"givenName", "familyName"};
    public static String[] uiPatientFieldsToBeGenerated = new String[]{"gender", "birthdate", "birthdateEstimated",
            "dead"};
    public static String[] uiUnknownPatientFieldsToBeGenerated = new String[]{"gender", "birthdateEstimated",
            "dead", "age"};

    private final SelenideElement registerPatientButton = $(By.xpath("//button[text()='Register patient']"));
    private final SelenideElement cancelButton = $(By.xpath("//button[text()='Cancel']"));
    private final SelenideElement patientNameIsKnownYesButton = $(By.xpath("//div/span[text()=\"Patient's Name is Known?\"]/../following-sibling::div/button/span[text()='Yes']"));
    private final SelenideElement patientNameIsKnownNoButton = $(By.xpath("//div/span[text()=\"Patient's Name is Known?\"]/../following-sibling::div/button/span[text()='No']"));
    private final SelenideElement firstNameField = $("#givenName");
    private final SelenideElement middleNameField = $("#middleName");
    private final SelenideElement familyNameField = $("#familyName");
    private final ElementsCollection sexRadioButtons = $$("fieldset>div");
    private final SelenideElement dateOfBirthKnownYesButton = $(By.xpath("//div/span[text()='Date of Birth Known?']/../following-sibling::div/button/span[text()='Yes']"));
    private final SelenideElement dateOfBirthKnownNoButton = $(By.xpath("//div/span[text()='Date of Birth Known?']/../following-sibling::div/button/span[text()='No']"));
    private final SelenideElement birthdateDayPartOfField = $(By.xpath("//span[@role='spinbutton' and text()='dd']"));
    private final SelenideElement addressField = $("#address1");
    private final SelenideElement address2Field = $("#address2");
    private final SelenideElement cityVillageField = $("#cityVillage");
    private final SelenideElement stateProvinceField = $("#stateProvince");
    private final SelenideElement countryField = $("#country");
    private final SelenideElement postalCodeField = $("#postalCode");
    private final SelenideElement telephoneNumberField = $("#phone");
    private final SelenideElement estimateAgeInYearsField = $(By.xpath("//input[@id='yearsEstimated']"));
    private final SelenideElement estimateAgeInMonthsField = $("#monthsEstimated");

    @Override
    public String url() {
        return "/patient-registration";
    }

    @Override
    public PatientRegistrationPage checkItIsCorrectPage() {
        patientNameIsKnownYesButton.shouldBe(Condition.visible);

        return this;
    }

    public PatientRegistrationPage clickOnPatientNameIsKnownYesButton() {
        patientNameIsKnownYesButton.shouldBe(Condition.visible);
        patientNameIsKnownYesButton.click();

        return this;
    }

    public PatientRegistrationPage clickOnPatientNameIsKnownNoButton() {
        patientNameIsKnownNoButton.shouldBe(Condition.visible);
        patientNameIsKnownNoButton.click();

        firstNameField.shouldNotBe(Condition.visible);
        middleNameField.shouldNotBe(Condition.visible);
        familyNameField.shouldNotBe(Condition.visible);

        return this;
    }

    public PatientRegistrationPage clickOnRegisterPatientButton() {
        registerPatientButton.shouldBe(Condition.visible);
        registerPatientButton.click();

        return this;
    }

    public PatientRegistrationPage clickOnCancelButton() {
        cancelButton.shouldBe(Condition.visible);
        cancelButton.click();

        return this;
    }

    public PatientRegistrationPage selectGender(String gender) {
        sexRadioButtons.filterBy(Condition.visible).shouldHave(CollectionCondition.size(sexRadioButtons.size()));
        sexRadioButtons.findBy(Condition.text(gender)).click();

        return this;
    }

    public PatientRegistrationPage populateFirstNameField(String firstname) {
        firstNameField.shouldBe(Condition.visible);
        firstNameField.sendKeys(firstname);

        return this;
    }

    public PatientRegistrationPage populateMiddleNameField(String middleName) {
        middleNameField.shouldBe(Condition.visible);
        middleNameField.sendKeys(middleName);

        return this;
    }

    public PatientRegistrationPage populateFamilyNameField(String familyName) {
        familyNameField.shouldBe(Condition.visible);
        familyNameField.sendKeys(familyName);

        return this;
    }

    public PatientRegistrationPage populateBirthdayField(String dateOfBirth) {
        birthdateDayPartOfField.shouldBe(Condition.visible);
        birthdateDayPartOfField.sendKeys(dateOfBirth);

        return this;
    }

    public PatientRegistrationPage clickOnDateOfBirthKnownYesButton() {
        dateOfBirthKnownYesButton.shouldBe(Condition.visible);
        dateOfBirthKnownYesButton.click();

        return this;
    }

    public PatientRegistrationPage clickOnDateOfBirthKnownNoButton() {
        dateOfBirthKnownNoButton.shouldBe(Condition.visible);
        dateOfBirthKnownNoButton.click();

        birthdateDayPartOfField.shouldNotBe(Condition.visible);
        estimateAgeInYearsField.shouldBe(Condition.visible);
        estimateAgeInMonthsField.shouldBe(Condition.visible);

        return this;
    }

    public PatientRegistrationPage populateAddressField(String address) {
        addressField.shouldBe(Condition.visible);
        addressField.sendKeys(address);

        return this;
    }

    public PatientRegistrationPage populateAddress2Field(String address) {
        address2Field.shouldBe(Condition.visible);
        address2Field.sendKeys(address);

        return this;
    }

    public PatientRegistrationPage populateCityVillageField(String cityVillage) {
        cityVillageField.shouldBe(Condition.visible);
        cityVillageField.sendKeys(cityVillage);

        return this;
    }

    public PatientRegistrationPage populateCountryField(String country) {
        countryField.shouldBe(Condition.visible);
        countryField.sendKeys(country);

        return this;
    }

    public PatientRegistrationPage populateStateProvinceField(String stateProvince) {
        stateProvinceField.shouldBe(Condition.visible);
        stateProvinceField.sendKeys(stateProvince);

        return this;
    }

    public PatientRegistrationPage populatePostalCodeField(String postalCode) {
        postalCodeField.shouldBe(Condition.visible);
        postalCodeField.sendKeys(postalCode);

        return this;
    }

    public PatientRegistrationPage populateTelephoneNumberField(String number) {
        telephoneNumberField.shouldBe(Condition.visible);
        telephoneNumberField.sendKeys(number);

        return this;
    }

    public PatientRegistrationPage populateAgeInYearsField(Integer ageInYears) {
        estimateAgeInYearsField.shouldBe(Condition.interactable).clear();
        estimateAgeInYearsField.sendKeys(String.valueOf(ageInYears));

        return this;
    }

    public PatientSummaryPage registerPatientWithAllFieldsPopulatedCorrectly(RegisterPatientUi patient) {
        clickOnPatientNameIsKnownYesButton();
        populateFirstNameField(patient.getNames().getFirst().getGivenName());
        populateMiddleNameField(patient.getNames().getFirst().getMiddleName());
        populateFamilyNameField(patient.getNames().getFirst().getFamilyName());
        selectGender(patient.getGender());
        clickOnDateOfBirthKnownYesButton();
        populateBirthdayField(patient.getBirthdate());
        populateAddressField(patient.getAddresses().getFirst().getAddress1());
        populateAddress2Field(patient.getAddresses().getFirst().getAddress2());
        populateCityVillageField(patient.getAddresses().getFirst().getCityVillage());
        populateStateProvinceField(patient.getAddresses().getFirst().getStateProvince());
        populateCountryField(patient.getAddresses().getFirst().getCountry());
        populatePostalCodeField(patient.getAddresses().getFirst().getPostalCode());
        populateTelephoneNumberField(patient.getAttributes().getFirst().getValue());

        clickOnRegisterPatientButton();

        return getPage(PatientSummaryPage.class);
    }

    public PatientSummaryPage registerPatientWithValidMandatoryFields(RegisterMandatoryFieldsPatientUi patient) {
        clickOnPatientNameIsKnownYesButton();
        populateFirstNameField(patient.getNames().getFirst().getGivenName());
        populateFamilyNameField(patient.getNames().getFirst().getFamilyName());
        selectGender(patient.getGender());
        clickOnDateOfBirthKnownYesButton();
        populateBirthdayField(patient.getBirthdate());

        clickOnRegisterPatientButton();

        return getPage(PatientSummaryPage.class);
    }

    public PatientSummaryPage registerUnknownPatientTest(RegisterUnknownPatientUi patient) {
        clickOnPatientNameIsKnownNoButton();
        selectGender(patient.getGender());
        clickOnDateOfBirthKnownNoButton();
        populateAgeInYearsField(patient.getAge());

        clickOnRegisterPatientButton();

        return getPage(PatientSummaryPage.class);
    }
}
