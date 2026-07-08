package api;

import api.constants.Constants;
import api.models.*;
import api.models.comparison.ModelAssertions;
import api.models.enums.ClinicName;
import api.models.enums.Gender;
import api.requests.Endpoint;
import api.requests.skeleton.requesters.CrudRequester;
import api.requests.skeleton.requesters.ValidatedCrudRequester;
import api.requests.specs.RequestSpecs;
import api.requests.specs.ResponseSpecs;
import api.requests.steps.AdminSteps;
import api.utils.EntityTestUtils;
import common.annotations.AutoCleanup;
import common.generators.PartialEntityGenerator;
import common.generators.RandomDataGenerator;
import common.utils.DateUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.stream.Stream;

import static api.utils.DisplayFormatterUtils.personDisplayFormatter;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CreatePatientTest extends BaseTest {
    private static final boolean PREFERRED_IDENTIFIER_TRUE = Constants.PREFERRED_IDENTIFIER_TRUE;
    private static final String[] NAMES_FIELDS_TO_BE_GENERATED = Constants.nameFieldsToBeGenerated;

    @Test
    public void knownPatientCanBeCreatedWithOnlyMandatoryDataTest() {
        PersonName personName = PartialEntityGenerator.generate(PersonName.class, NAMES_FIELDS_TO_BE_GENERATED);

        CreatePersonRequest person = CreatePersonRequest.builder()
                .gender(Gender.M.toString())
                .names(List.of(personName)).build();

        IdentifiersForPatientCreation identifiers = AdminSteps.prepareIdentifiersForPatientCreation(
                ClinicName.OUTPATIENT.getClinicName(), PREFERRED_IDENTIFIER_TRUE);

        CreatePatientRequest createPatientRequest = CreatePatientRequest.builder()
                .identifiers(List.of(identifiers))
                .person(person)
                .build();

        CreatePatientResponse createdPatient = new ValidatedCrudRequester<CreatePatientResponse>(
                RequestSpecs.adminSpec(),
                Endpoint.PATIENT,
                ResponseSpecs.requestReturnsCreated())
                .post(createPatientRequest);

        softly.assertThat(createdPatient.getUuid()).isNotEmpty();
        softly.assertThat(createdPatient.getUuid()).isEqualTo(createdPatient.getPerson().getUuid());
        softly.assertThat(createdPatient.getPerson().getGender()).isEqualTo(person.getGender());
        softly.assertThat(createdPatient.getDisplay())
                .isEqualTo(String.format("%s - %s", identifiers.getIdentifier(), personDisplayFormatter(personName)));

        CreatePatientResponse foundPatient = AdminSteps.findPatientByUuid(createdPatient.getUuid());

        assertThat(createdPatient).usingRecursiveComparison().isEqualTo(foundPatient);
    }

    @Test
    public void knownPatientCanBeCreatedWithValidDataTest() {
        final String identifierResponsePrefix = "OpenMRS ID = ";

        PersonName personName = PartialEntityGenerator.generate(PersonName.class, NAMES_FIELDS_TO_BE_GENERATED);

        CreatePersonRequest person = PartialEntityGenerator.generate(CreatePersonRequest.class, Constants.personFieldsToBeGenerated);
        person.setNames(List.of(personName));
        person.setBirthdate(RandomDataGenerator.generateValidDate());
        int expectedDate = DateUtils.calculateAge(person.getBirthdate());

        IdentifiersForPatientCreation identifiers = AdminSteps.prepareIdentifiersForPatientCreation(
                ClinicName.OUTPATIENT.getClinicName(), PREFERRED_IDENTIFIER_TRUE);

        CreatePatientRequest createPatientRequest = CreatePatientRequest.builder()
                .identifiers(List.of(identifiers))
                .person(person)
                .build();

        CreatePatientResponse createdPatient = new ValidatedCrudRequester<CreatePatientResponse>(
                RequestSpecs.adminSpec(),
                Endpoint.PATIENT,
                ResponseSpecs.requestReturnsCreated())
                .post(createPatientRequest);

        ModelAssertions.assertThatModels(createPatientRequest, createdPatient).match();
        softly.assertThat(createdPatient.getUuid()).isNotEmpty();
        softly.assertThat(createdPatient.getUuid()).isEqualTo(createdPatient.getPerson().getUuid());
        softly.assertThat(createdPatient.getDisplay())
                .isEqualTo(String.format("%s - %s", identifiers.getIdentifier(), personDisplayFormatter(personName)));
        softly.assertThat(createdPatient.getIdentifiers().getFirst().getDisplay())
                .isEqualTo("%s%s", identifierResponsePrefix,
                        createPatientRequest.getIdentifiers().getFirst().getIdentifier());
        softly.assertThat(createdPatient.getPerson().getDisplay())
                .isEqualTo(personDisplayFormatter(createPatientRequest.getPerson().getNames().getFirst()));
        softly.assertThat(expectedDate).isEqualTo(createdPatient.getPerson().getAge());

        AddressResponse personAddress = AdminSteps.getPersonAddress(createdPatient.getPerson().getUuid());
        ModelAssertions.assertThatModels(person, personAddress).match();

        CreatePatientResponse foundPatient = AdminSteps.findPatientByUuid(createdPatient.getUuid());

        assertThat(createdPatient).usingRecursiveComparison().isEqualTo(foundPatient);
    }

    @Test
    public void patientCannotBeCreatedWithEmptyGivenName() {
        final String emptyGivenName = "";

        PersonName personName = PartialEntityGenerator.generate(PersonName.class, NAMES_FIELDS_TO_BE_GENERATED);
        personName.setGivenName(emptyGivenName);

        CreatePersonRequest person = PartialEntityGenerator.generate(CreatePersonRequest.class, Constants.personFieldsToBeGenerated);
        person.setNames(List.of(personName));
        person.setBirthdate(RandomDataGenerator.generateValidDate());

        IdentifiersForPatientCreation identifiers = AdminSteps.prepareIdentifiersForPatientCreation(
                ClinicName.OUTPATIENT.getClinicName(), PREFERRED_IDENTIFIER_TRUE);

        CreatePatientRequest createPatientRequest = CreatePatientRequest.builder()
                .identifiers(List.of(identifiers))
                .person(person)
                .build();

        new CrudRequester(
                RequestSpecs.adminSpec(),
                Endpoint.PATIENT,
                ResponseSpecs.requestReturnBadRequestForIncorrectName(ResponseSpecs.GIVEN_NAME_FIELD, ResponseSpecs.YOU_MUST_DEFINE_THE_GIVEN_NAME_ERROR))
                .post(createPatientRequest);

        List<CreatePatientResponse> list = AdminSteps
                .searchPatientsByString(createPatientRequest.getPerson().getNames().getFirst().getGivenName()).stream()
                .filter(patient -> patient.getIdentifiers().getFirst().getDisplay()
                        .contains(identifiers.getIdentifier())).toList();

        assertTrue(list.isEmpty());
    }

    private static Stream<Arguments> emptyNames() {
        return Stream.of(
                EntityTestUtils.entityWithValidValue(PersonName.class, NAMES_FIELDS_TO_BE_GENERATED,
                        n -> n.setMiddleName("")),
                EntityTestUtils.entityWithValidValue(PersonName.class, NAMES_FIELDS_TO_BE_GENERATED,
                        n -> n.setFamilyName(""))
        );
    }

    @ParameterizedTest
    @MethodSource("emptyNames")
    public void patientCanBeCreatedWithEmptyMiddleOrGivenName(PersonName personName) {
        CreatePersonRequest person = PartialEntityGenerator.generate(CreatePersonRequest.class, Constants.personFieldsToBeGenerated);
        person.setNames(List.of(personName));
        person.setBirthdate(RandomDataGenerator.generateValidDate());

        IdentifiersForPatientCreation identifiers = AdminSteps.prepareIdentifiersForPatientCreation(
                ClinicName.OUTPATIENT.getClinicName(), PREFERRED_IDENTIFIER_TRUE);

        CreatePatientRequest createPatientRequest = CreatePatientRequest.builder()
                .identifiers(List.of(identifiers))
                .person(person)
                .build();

        CreatePatientResponse createdPatient = new ValidatedCrudRequester<CreatePatientResponse>(
                RequestSpecs.adminSpec(),
                Endpoint.PATIENT,
                ResponseSpecs.requestReturnsCreated())
                .post(createPatientRequest);

        ModelAssertions.assertThatModels(createPatientRequest, createdPatient).match();
        softly.assertThat(createdPatient.getUuid()).isNotEmpty();
        softly.assertThat(createdPatient.getDisplay())
                .isEqualTo(String.format("%s - %s", identifiers.getIdentifier(), personDisplayFormatter(personName)));
        softly.assertThat(createdPatient.getPerson().getDisplay())
                .isEqualTo(personDisplayFormatter(createPatientRequest.getPerson().getNames().getFirst()));
        softly.assertThat(createdPatient.getPerson().getPreferredName().getDisplay())
                .isEqualTo(personDisplayFormatter(createPatientRequest.getPerson().getNames().getFirst()));

        CreatePatientResponse foundPatient = AdminSteps.findPatientByUuid(createdPatient.getUuid());

        assertThat(createdPatient).usingRecursiveComparison().isEqualTo(foundPatient);
    }
}
