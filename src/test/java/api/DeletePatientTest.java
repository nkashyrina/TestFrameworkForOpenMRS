package api;

import api.requests.Endpoint;
import api.requests.skeleton.requesters.CrudRequester;
import api.requests.specs.RequestSpecs;
import api.requests.specs.ResponseSpecs;
import api.requests.steps.AdminSteps;
import common.annotations.SkipAutoCleanup;
import org.junit.jupiter.api.Test;

import static api.constants.Constants.PATH_PARAM_PURGE;
import static org.assertj.core.api.Assertions.assertThat;

public class DeletePatientTest extends BaseTest {

    @Test
    @SkipAutoCleanup
    void patientCanBeSoftDeletedTest() {
        String createdUuid = AdminSteps.createPatient().getUuid();
        new CrudRequester(
                RequestSpecs.adminSpec(),
                Endpoint.PATIENT_DELETE,
                ResponseSpecs.requestReturnsNoContent())
                .delete(createdUuid);

        assertThat(AdminSteps.findPatientByUuid(createdUuid).getDisplay()).isEmpty();
    }

    @Test
    @SkipAutoCleanup
    void patientCanBeHardDeletedTest() {
        String createdUuid = AdminSteps.createPatient().getUuid();
        new CrudRequester(
                RequestSpecs.adminSpec(),
                Endpoint.PATIENT_DELETE,
                ResponseSpecs.requestReturnsNoContent())
                .delete(createdUuid, PATH_PARAM_PURGE);

        assertThat(AdminSteps.findDeletedPatientByUuidReturnsError(createdUuid).getError().getMessage()).isEqualTo(ResponseSpecs.OBJECT_WITH_GIVEN_UUID_DOES_NOT_EXIST);
    }
}
