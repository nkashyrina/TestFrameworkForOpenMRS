package api;

import api.models.*;
import api.models.comparison.ModelAssertions;
import api.models.enums.ClinicName;
import api.models.visit.CreateVisitRequest;
import api.models.visit.CreateVisitResponse;
import api.requests.Endpoint;
import api.requests.skeleton.requesters.CrudRequester;
import api.requests.skeleton.requesters.ValidatedCrudRequester;
import api.models.enums.VisitTypeEnum;
import api.requests.specs.RequestSpecs;
import api.requests.specs.ResponseSpecs;
import api.requests.steps.AdminSteps;
import common.annotations.SkipAutoCleanup;
import common.generators.RandomDataGenerator;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class VisitTest extends BaseTest {

    @Test
    public void createVisitTest() {
        CreatePatientResponse patient = AdminSteps.createPatient();

        CreateVisitRequest createRequest = CreateVisitRequest.builder()
                .patient(patient.getUuid())
                .visitType(AdminSteps.getVisitTypeUuid(VisitTypeEnum.FACILITY_VISIT))
                .startDatetime(RandomDataGenerator.generateVisitStartDatetime())
                .location(AdminSteps.getLocationUuidByName(ClinicName.OUTPATIENT.getClinicName()))
                .indication(RandomDataGenerator.generateVisitIndication())
                .build();

        CreateVisitResponse createdVisit = new ValidatedCrudRequester<CreateVisitResponse>(
                RequestSpecs.adminSpec(),
                Endpoint.VISIT,
                ResponseSpecs.requestReturnsCreated())
                .post(createRequest);

        CreateVisitResponse foundVisit = new ValidatedCrudRequester<CreateVisitResponse>(
                RequestSpecs.adminSpec(),
                Endpoint.VISIT_BY_UUID,
                ResponseSpecs.requestReturnsOK())
                .get(createdVisit.getUuid(), CreateVisitResponse.class);

        ModelAssertions.assertThatModels(createRequest, foundVisit).match();
    }

    @Test
    public void searchRecentVisitsTest() {
        CreatePatientResponse patient = AdminSteps.createPatient();
        CreateVisitResponse createdVisit = AdminSteps.createVisit(patient);

        List<CreateVisitResponse> foundVisits = AdminSteps.getVisitsForPatient(patient.getUuid());

        assertThat(foundVisits)
                .as("Created visit should be in search results")
                .extracting(CreateVisitResponse::getUuid)
                .contains(createdVisit.getUuid());
    }

    @Test
    public void updateVisitStartDatetimeTest() {
        CreatePatientResponse patient = AdminSteps.createPatient();
        CreateVisitResponse createdVisit = AdminSteps.createVisit(patient);

        String newStartDatetime = RandomDataGenerator.generateVisitStartDatetime();

        AdminSteps.updateVisit(createdVisit.getUuid(), newStartDatetime);

        CreateVisitResponse updatedVisit = new ValidatedCrudRequester<CreateVisitResponse>(
                RequestSpecs.adminSpec(),
                Endpoint.VISIT_BY_UUID,
                ResponseSpecs.requestReturnsOK())
                .get(createdVisit.getUuid(), CreateVisitResponse.class);

        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(updatedVisit.getStartDatetime()).contains(newStartDatetime.substring(0, 10));
        });
    }

    @Test
    @SkipAutoCleanup
    public void deleteVisitTest() {
        CreatePatientResponse patient = AdminSteps.createPatient();

        CreateVisitRequest createRequest = CreateVisitRequest.builder()
                .patient(patient.getUuid())
                .visitType(AdminSteps.getVisitTypeUuid(VisitTypeEnum.FACILITY_VISIT))
                .startDatetime(RandomDataGenerator.generateVisitStartDatetime())
                .location(AdminSteps.getLocationUuidByName(ClinicName.OUTPATIENT.getClinicName()))
                .indication(RandomDataGenerator.generateVisitIndication())
                .build();

        CreateVisitResponse createdVisit = new ValidatedCrudRequester<CreateVisitResponse>(
                RequestSpecs.adminSpec(),
                Endpoint.VISIT,
                ResponseSpecs.requestReturnsCreated())
                .post(createRequest);

        new CrudRequester(
                RequestSpecs.adminSpec(),
                Endpoint.VISIT_BY_UUID,
                ResponseSpecs.requestReturnsNoContent())
                .delete(createdVisit.getUuid(), true);

        Assertions.assertDoesNotThrow(() ->
                new ValidatedCrudRequester<CreateVisitResponse>(
                        RequestSpecs.adminSpec(),
                        Endpoint.VISIT_BY_UUID,
                        ResponseSpecs.requestReturnsNotFoundWithMessage("Object with given uuid doesn't exist [null]"))
                        .get(createdVisit.getUuid(), CreateVisitResponse.class)
        );
    }
}

