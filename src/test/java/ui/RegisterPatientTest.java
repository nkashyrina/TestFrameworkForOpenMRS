package ui;

import api.models.AddressResponse;
import api.models.CreatePatientResponse;
import api.models.comparison.ModelAssertions;
import api.models.ui.GenderUi;
import api.models.ui.RegisterMandatoryFieldsPatientUi;
import api.models.ui.RegisterPatientUi;
import api.models.ui.RegisterUnknownPatientUi;
import api.requests.steps.AdminSteps;
import common.annotations.AdminSession;
import common.annotations.Skip;
import common.helpers.StepLogger;
import common.utils.DateUtils;
import org.junit.jupiter.api.Test;
import ui.pages.PatientSummaryPage;
import ui.pages.PickLocationPage;

import static api.utils.DisplayFormatterUtils.personDisplayFormatter;

public class RegisterPatientTest extends BaseUiTest {
    @Test
    @AdminSession
    public void knownPatientCanBeRegisteredWithAllValidDataTest() {
        RegisterPatientUi patient = AdminSteps.createPatientForUi();

        String patientUuid = StepLogger.log("Register a patient and check all values correspond to" +
                " the fields on Patient Summary Page", () -> {
            return new PickLocationPage().open()
                    .selectOutpatientLocationAndConfirm()
                    .header.clickAddPatientButton()
                    .registerPatientWithAllFieldsPopulatedCorrectly(patient)
                    .verifySuccessNotification()

                    .checkPatientNameIsEqualTo(personDisplayFormatter(patient.getNames().getFirst()))
                    .checkIdPrefixIsEqualTo(PatientSummaryPage.OPEN_MRS_ID_TEXT)
                    .checkBirthDateIsEqualTo(patient.getBirthdate())
                    .checkGenderIsEqualTo(patient.getGender())
                    .checkGenderIconIsCorrect()

                    .clickShowMoreButton()
                    .getAddressComponent().checkAllAddressFieldsAreCorrect(patient.getAddresses().getFirst())
                    .getContactDetailsComponent().checkTelephoneNumberIsEqualTo(patient.getAttributes().getFirst().getValue())
                    .getPatientUuid();
        });

        String openMrsIdText = new PatientSummaryPage().getOpenMrsIdTextInApiFormat();
        CreatePatientResponse foundPatient = AdminSteps.findPatientByUuid(patientUuid);
        new PatientSummaryPage().addPatientToEntityStorage(foundPatient);
        AddressResponse patientAddress = AdminSteps.getPersonAddress(foundPatient.getPerson().getUuid());

        StepLogger.log("Set gender to API format to compare UI DTO with API response", () -> {
            patient.setGender(GenderUi.toShortGender(patient.getGender()));
        });

        StepLogger.log("Validate patient is created on API side and has the same fields populated", () -> {
            ModelAssertions.assertThatModels(foundPatient, patient).match();
            softly.assertThat(openMrsIdText).isEqualTo(foundPatient.getIdentifiers().getFirst().getDisplay());
            softly.assertThat(personDisplayFormatter(patient.getNames().getFirst()))
                    .isEqualTo(foundPatient.getPerson().getPreferredName().getDisplay());
            ModelAssertions.assertThatModels(patient, patientAddress);
        });
    }

    @Test
    @AdminSession
    public void knownPatientCanBeRegisteredWithMandatoryValidDataTest() {
        RegisterMandatoryFieldsPatientUi patient = AdminSteps.createPatientWithMandatoryFieldsForUi();

        String patientUuid = StepLogger.log("Register a patient and check all values correspond to" +
                " the fields on Patient Summary Page", () -> {
            return new PickLocationPage().open()
                    .selectOutpatientLocationAndConfirm()
                    .header.clickAddPatientButton()
                    .registerPatientWithValidMandatoryFields(patient)
                    .verifySuccessNotification()

                    .checkPatientNameIsEqualTo(personDisplayFormatter(patient.getNames().getFirst()))
                    .checkIdPrefixIsEqualTo(PatientSummaryPage.OPEN_MRS_ID_TEXT)
                    .checkBirthDateIsEqualTo(patient.getBirthdate())
                    .checkGenderIsEqualTo(patient.getGender())
                    .checkGenderIconIsCorrect()

                    .clickShowMoreButton()
                    .getAddressComponent().checkAddressSectionIsEmpty()
                    .getContactDetailsComponent().checkContactDetailsSectionIsEmpty()
                    .getPatientUuid();
        });

        String openMrsIdText = new PatientSummaryPage().getOpenMrsIdTextInApiFormat();
        CreatePatientResponse foundPatient = AdminSteps.findPatientByUuid(patientUuid);
        new PatientSummaryPage().addPatientToEntityStorage(foundPatient);

        StepLogger.log("Set gender to API format to compare UI DTO with API response", () -> {
            patient.setGender(GenderUi.toShortGender(patient.getGender()));
        });

        StepLogger.log("Validate patient is created on API side and has the same fields populated", () -> {
            ModelAssertions.assertThatModels(foundPatient, patient).match();
            softly.assertThat(openMrsIdText).isEqualTo(foundPatient.getIdentifiers().getFirst().getDisplay());
            softly.assertThat(personDisplayFormatter(patient.getNames().getFirst()))
                    .isEqualTo(foundPatient.getPerson().getPreferredName().getDisplay());
            softly.assertThat(foundPatient.getPerson().getPreferredAddress()).isNull();
            softly.assertThat(foundPatient.getPerson().getAttributes()).isEmpty();
        });
    }

    @Skip(reason = "Bug(Inconsistency): when aprox. date is fewer than 6 only year is shown, " +
            "when greater month and year")
    @Test
    @AdminSession
    public void unknownPatientCanBeRegisteredWithValidDataTest() {
        RegisterUnknownPatientUi patient = AdminSteps.createUnknownPatientForUi();

        String patientUuid = StepLogger.log("Register a patient and check all values correspond to" +
                " the fields on Patient Summary Page", () -> {
            return new PickLocationPage().open()
                    .selectOutpatientLocationAndConfirm()
                    .header.clickAddPatientButton()
                    .registerUnknownPatientTest(patient)

                    .checkPatientNameIsEqualTo(personDisplayFormatter(patient.getNames().getFirst()))
                    .checkIdPrefixIsEqualTo(PatientSummaryPage.OPEN_MRS_ID_TEXT)
                    .checkEstimatedBirthDateIsEqualTo(patient.getAge())
                    .checkGenderIsEqualTo(patient.getGender())
                    .checkGenderIconIsCorrect()

                    .clickShowMoreButton()
                    .getAddressComponent().checkAddressSectionIsEmpty()
                    .getContactDetailsComponent().checkContactDetailsSectionIsEmpty()
                    .getPatientUuid();
        });

        String openMrsIdText = new PatientSummaryPage().getOpenMrsIdTextInApiFormat();
        CreatePatientResponse foundPatient = AdminSteps.findPatientByUuid(patientUuid);
        new PatientSummaryPage().addPatientToEntityStorage(foundPatient);

        StepLogger.log("Set gender and birthdate to API format to compare UI DTO with API response", () -> {
            patient.setGender(GenderUi.toShortGender(patient.getGender()));
            patient.setBirthdate(DateUtils.convertMmmYyyyToFullDate(patient.getBirthdate()));
        });

        StepLogger.log("Validate patient is created on API side and has the same fields populated", () -> {
            ModelAssertions.assertThatModels(foundPatient, patient).match();
            softly.assertThat(openMrsIdText).isEqualTo(foundPatient.getIdentifiers().getFirst().getDisplay());
            softly.assertThat(personDisplayFormatter(patient.getNames().getFirst()))
                    .isEqualTo(foundPatient.getPerson().getPreferredName().getDisplay());
            softly.assertThat(foundPatient.getPerson().getPreferredAddress()).isNull();
            softly.assertThat(foundPatient.getPerson().getAttributes()).isEmpty();
        });
    }
}
