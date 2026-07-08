package api;

import api.models.CreatePatientRequest;
import api.models.CreatePatientResponse;
import api.models.comparison.ModelAssertions;
import api.models.patient.UpdatePatientRequest;
import api.requests.Endpoint;
import api.requests.skeleton.requesters.ValidatedCrudRequester;
import api.requests.specs.RequestSpecs;
import api.requests.specs.ResponseSpecs;
import api.requests.steps.AdminSteps;
import common.annotations.Skip;
import common.generators.RandomDataGenerator;
import org.junit.jupiter.api.Test;

import static api.utils.DisplayFormatterUtils.personDisplayFormatter;
import static org.assertj.core.api.Assertions.assertThat;

public class UpdatePatientTest extends BaseTest {
    @Skip(reason = "Bug: uuid is changed for Address even when address field is not even updated")
    @Test
    public void patientCanBeUpdatedSuccessfully() {
        CreatePatientRequest patientRequest = AdminSteps.createPatientRequest();
        CreatePatientResponse createdPatient = AdminSteps.createPatient(patientRequest);

        UpdatePatientRequest updatePatientRequest = AdminSteps.prepareUpdatePatientRequest(patientRequest, createdPatient);
        updatePatientRequest.getPerson().getNames().getFirst()
                .setGivenName(RandomDataGenerator.generateName());

        CreatePatientResponse updatedPatient = new ValidatedCrudRequester<CreatePatientResponse>(
                RequestSpecs.adminSpec(),
                Endpoint.PATIENT_UPDATE,
                ResponseSpecs.requestReturnsOK()
        ).post(updatePatientRequest, createdPatient.getUuid());

        ModelAssertions.assertThatModels(createdPatient, updatedPatient).match();
        softly.assertThat(updatedPatient.getDisplay())
                .isEqualTo(String.format("%s - %s",
                        updatePatientRequest.getIdentifiers().getFirst().getIdentifier(),
                        personDisplayFormatter(updatePatientRequest.getPerson().getNames().getFirst())));
        softly.assertThat(updatedPatient.getPerson().getDisplay())
                .isEqualTo(personDisplayFormatter(updatePatientRequest.getPerson().getNames().getFirst()));
        softly.assertThat(updatedPatient.getPerson().getPreferredName().getDisplay())
                .isEqualTo(personDisplayFormatter(updatePatientRequest.getPerson().getNames().getFirst()));

        CreatePatientResponse foundUpdatedPatient = AdminSteps.findPatientByUuid(updatedPatient.getUuid());

        assertThat(foundUpdatedPatient).usingRecursiveComparison().isEqualTo(updatedPatient);
    }
}
