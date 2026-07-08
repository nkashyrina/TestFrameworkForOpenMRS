package ui;

import api.models.CreatePatientResponse;
import api.models.ui.VisitTab;
import api.models.visit.CreateVisitResponse;
import api.models.enums.VisitTypeEnum;
import api.requests.steps.AdminSteps;
import com.codeborne.selenide.WebDriverRunner;
import common.annotations.AdminSession;
import common.annotations.Skip;
import org.junit.jupiter.api.Test;
import ui.pages.PickLocationPage;
import ui.pages.VisitPage;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class VisitTest extends BaseUiTest {

    private VisitPage startNewVisit(String patientUuid, VisitTypeEnum visitType) {
        return new VisitPage().open(patientUuid)
                .waitPatientSummaryLoaded()
                .openActionsMenu()
                .selectAddVisit()
                .waitStartVisitModal()
                .selectVisitTab(VisitTab.NEW)
                .selectUbuntuHospitalLocation()
                .selectVisitType(visitType)
                .confirmStartVisitAndWaitForClose()
                .checkActiveVisitIsStarted()
                .waitAfterVisitStarted();
    }

    private VisitPage openStartVisitModal(String patientUuid) {
        return new VisitPage().open(patientUuid)
                .waitPatientSummaryLoaded()
                .openActionsMenu()
                .selectAddVisit()
                .waitStartVisitModal()
                .selectVisitTab(VisitTab.NEW)
                .selectUbuntuHospitalLocation();
    }

    @Test
    @AdminSession
    public void startVisit() {
        CreatePatientResponse createdPatient = AdminSteps.createPatient();
        String patientUuid = createdPatient.getUuid();

        new PickLocationPage()
                .open()
                .selectClinicLocation()
                .confirmClinicLocation();

        startNewVisit(patientUuid, VisitTypeEnum.FACILITY_VISIT);

        assertThat(WebDriverRunner.url())
                .as("URL should contain patient UUID")
                .contains(patientUuid);

        List<CreateVisitResponse> apiVisits = AdminSteps.getVisitsForPatient(patientUuid);

        assertThat(apiVisits)
                .as("Visit started from UI should exist in API response")
                .isNotEmpty()
                .anySatisfy(visit -> assertThat(visit.getPatient().getUuid())
                        .isEqualTo(patientUuid));
    }

    @Skip(reason = "flaky test, that should be updated")
    @Test
    @AdminSession
    public void endVisit() {
        CreatePatientResponse createdPatient = AdminSteps.createPatient();
        String patientUuid = createdPatient.getUuid();

        new PickLocationPage()
                .open()
                .selectClinicLocation()
                .confirmClinicLocation();

        VisitPage visitPage = startNewVisit(patientUuid, VisitTypeEnum.FACILITY_VISIT);

        visitPage.openActionsMenu()
                .selectEndActiveVisit()
                .waitEndVisitConfirmationModal()
                .confirmEndVisit()
                .checkVisitEndedSuccessfully()
                .checkNoActiveVisitTag();

        assertThat(WebDriverRunner.url())
                .as("URL should contain patient UUID")
                .contains(patientUuid);

        List<CreateVisitResponse> apiVisits = AdminSteps.getVisitsForPatient(patientUuid);

        assertThat(apiVisits)
                .as("Ended visit should still exist in API")
                .isNotEmpty()
                .anySatisfy(visit -> {
                    assertThat(visit.getPatient().getUuid())
                            .as("Patient UUID should match")
                            .isEqualTo(patientUuid);

                    assertThat(visit.getStopDatetime())
                            .as("Visit should have stopDatetime after ending")
                            .isNotNull()
                            .isNotBlank();

                    assertThat(visit.isVoided())
                            .as("Visit should NOT be voided after ending")
                            .isFalse();
                });
    }

    @Test
    @AdminSession
    public void shouldDisplayNoUpcomingAppointmentsMessage() {
        CreatePatientResponse createdPatient = AdminSteps.createPatient();
        String patientUuid = createdPatient.getUuid();

        new PickLocationPage()
                .open()
                .selectClinicLocation()
                .confirmClinicLocation();
        new VisitPage().open(patientUuid)
                .waitPatientSummaryLoaded()
                .openActionsMenu()
                .selectAddVisit()
                .waitStartVisitModal()
                .checkNoUpcomingAppointmentsMessage();
    }

    @Test
    @AdminSession
    public void startVisitWithoutVisitTypeShouldShowError() {
        CreatePatientResponse createdPatient = AdminSteps.createPatient();
        String patientUuid = createdPatient.getUuid();

        new PickLocationPage()
                .open()
                .selectClinicLocation()
                .confirmClinicLocation();

        new VisitPage().open(patientUuid)
                .waitPatientSummaryLoaded()
                .openActionsMenu()
                .selectAddVisit()
                .waitStartVisitModal()
                .selectVisitTab(VisitTab.NEW)
                .selectUbuntuHospitalLocation()
                .confirmStartVisit()
                .checkStartVisitErrorIsDisplayed();
    }

    @Test
    @AdminSession
    public void startVisitAndDiscardShouldNotCreateActiveVisit() {
        CreatePatientResponse createdPatient = AdminSteps.createPatient();
        String patientUuid = createdPatient.getUuid();

        new PickLocationPage()
                .open()
                .selectClinicLocation()
                .confirmClinicLocation();

        VisitPage visitPage = openStartVisitModal(patientUuid);

        visitPage.selectVisitType(VisitTypeEnum.FACILITY_VISIT)
                .clickDiscard()
                .confirmDiscard()
                .checkDiscardModalOrFormClosed()
                .checkNoActiveVisitTag();

        assertThat(WebDriverRunner.url())
                .as("URL should contain patient UUID")
                .contains(patientUuid);

        List<CreateVisitResponse> apiVisits = AdminSteps.getVisitsForPatient(patientUuid);

        assertThat(apiVisits)
                .as("No visit should be created after Discard")
                .isEmpty();
    }

    @Test
    @AdminSession
    public void startVisitAndClickCancelShouldKeepModalOpen() {
        CreatePatientResponse createdPatient = AdminSteps.createPatient();
        String patientUuid = createdPatient.getUuid();

        new PickLocationPage()
                .open()
                .selectClinicLocation()
                .confirmClinicLocation();

        VisitPage visitPage = openStartVisitModal(patientUuid);

        visitPage.selectVisitType(VisitTypeEnum.FACILITY_VISIT)
                .clickDiscard()
                .clickCancel()
                .checkStartVisitModalStillOpen()
                .checkNoActiveVisitTag();

        assertThat(WebDriverRunner.url())
                .as("URL should contain patient UUID")
                .contains(patientUuid);

        List<CreateVisitResponse> apiVisits = AdminSteps.getVisitsForPatient(patientUuid);

        assertThat(apiVisits)
                .as("No visit should be created")
                .isEmpty();
    }

    @Test
    @AdminSession
    public void deleteActiveVisitShouldRemoveItSuccessfully() {
        CreatePatientResponse createdPatient = AdminSteps.createPatient();
        String patientUuid = createdPatient.getUuid();

        new PickLocationPage()
                .open()
                .selectClinicLocation()
                .confirmClinicLocation();

        VisitPage visitPage = startNewVisit(patientUuid, VisitTypeEnum.FACILITY_VISIT);

        visitPage.deleteActiveVisit()
                .waitDeleteVisitConfirmationModal()
                .confirmDeleteVisit()
                .checkVisitDeletedSuccessfully()
                .checkNoActiveVisitTag();

        assertThat(WebDriverRunner.url())
                .as("URL should contain patient UUID")
                .contains(patientUuid);

        List<CreateVisitResponse> apiVisits = AdminSteps.getVisitsForPatient(patientUuid);

        assertThat(apiVisits)
                .as("Visit should be completely deleted (not returned in API)")
                .isEmpty();
    }
}
