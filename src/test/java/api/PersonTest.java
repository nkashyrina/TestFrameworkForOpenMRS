package api;

import api.models.*;
import api.models.comparison.ModelAssertions;
import api.requests.Endpoint;
import api.requests.skeleton.requesters.CrudRequester;
import api.requests.skeleton.requesters.ValidatedCrudRequester;
import api.requests.specs.RequestSpecs;
import api.requests.specs.ResponseSpecs;
import api.requests.steps.AdminSteps;
import common.annotations.SkipAutoCleanup;
import common.generators.PartialEntityGenerator;
import common.generators.RandomDataGenerator;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static api.constants.Constants.PATH_PARAM_PURGE;
import static org.assertj.core.api.Assertions.assertThat;

public class PersonTest extends BaseTest {
    final static String[] fieldsToBeGenerated = new String[]{"givenName", "middleName", "familyName"};
    private final static List<String> createdUuids = new ArrayList<>();

    @Test
    public void positiveCreatePersonWithMandatoryFieldsTest() {
        PersonName personName = PartialEntityGenerator.generate(PersonName.class, fieldsToBeGenerated);
        CreatePersonRequest createPersonRequest = CreatePersonRequest.builder()
                .names(List.of(personName))
                .age(RandomDataGenerator.randomAge(0, 100))
                .gender(RandomDataGenerator.randomGender().toString())
                .build();

        CreatePersonResponse createdPerson = new ValidatedCrudRequester<CreatePersonResponse>(
                RequestSpecs.adminSpec(),
                Endpoint.PERSON,
                ResponseSpecs.requestReturnsCreated())
                .post(createPersonRequest);

        createdUuids.add(createdPerson.getUuid());
        CreatePersonResponse foundPerson = AdminSteps.findPersonByUuid(createdPerson.getUuid());
        ModelAssertions.assertThatModels(createdPerson, foundPerson).match();
    }

    public static Stream<Arguments> negativeCreatePersonData() {
        return Stream.of(
                Arguments.of("", "middle", "LastName", 21, "M", "names[0].givenName", "You must define the Given Name"),
                Arguments.of("First", "middle", "LastName", -2, "F", "birthdate", "Cannot be a date in the future"),
                Arguments.of("first", "middle", "LastName", 210, "U", "birthdate", "Nonsensical date, please check."));
    }

    @MethodSource("negativeCreatePersonData")
    @ParameterizedTest
    public void negativeCreatePersonTest(String firstName, String middleName, String lastName, int age, String gender, String errorMessage) {
        PersonName testName = PersonName.builder()
                .givenName(firstName)
                .middleName(middleName)
                .familyName(lastName)
                .build();

        CreatePersonRequest createPersonRequest = CreatePersonRequest.builder()
                .names(List.of(testName))
                .age(age)
                .gender(gender)
                .build();

        new CrudRequester(
                RequestSpecs.adminSpec(),
                Endpoint.PERSON_WITH_ERROR,
                ResponseSpecs.requestReturnsBadRequest())
                .post(createPersonRequest);
    }

    @Test
    public void positiveUpdatePersonMandatoryFieldsTest() {
        CreatePersonRequest createPersonRequest = AdminSteps.createPerson();
        CreatePersonResponse personBeforeUpdate = AdminSteps.createPerson(createPersonRequest);
        createdUuids.add(personBeforeUpdate.getUuid());
        String uuidForUpdate = personBeforeUpdate.getUuid();

        String newGender = RandomDataGenerator.randomGender(personBeforeUpdate.getGender()).toString();
        String newDate = RandomDataGenerator.randomDateBetween(LocalDate.parse("1980-06-15"), LocalDate.parse("1990-06-15"));

        PersonName personName = PartialEntityGenerator.generate(PersonName.class, fieldsToBeGenerated);
        CreatePersonRequest updateRequest = CreatePersonRequest.builder()
                .names(List.of(personName))
                .gender(newGender)
                .birthdate(newDate)
                .build();

        new ValidatedCrudRequester<CreatePersonResponse>(
                RequestSpecs.adminSpec(),
                Endpoint.PERSON_UPDATE,
                ResponseSpecs.requestReturnsOK())
                .post(updateRequest, uuidForUpdate);

        CreatePersonResponse personAfterUpdate = AdminSteps.findPersonByUuid(uuidForUpdate);

        softly.assertThat(personAfterUpdate.getDisplay())
                .as("Name should be updated")
                .contains(personName.getGivenName());

        softly.assertThat(personAfterUpdate.getDisplay())
                .as("MiddleName should be updated")
                .contains(personName.getMiddleName());

        softly.assertThat(personAfterUpdate.getDisplay())
                .as("LastName should be updated")
                .contains(personName.getFamilyName());

        softly.assertThat(personAfterUpdate.getGender())
                .as("Gender should have changed")
                .isNotEqualTo(personBeforeUpdate.getGender());

        softly.assertThat(personAfterUpdate.getDisplay())
                .as("Names should have changed")
                .isNotEqualTo(personBeforeUpdate.getDisplay());

        softly.assertThat(personAfterUpdate.getBirthdate())
                .as("Birthdate should not be the same as was before")
                .isNotEqualTo(personBeforeUpdate.getBirthdate());

        softly.assertThat(personAfterUpdate.getBirthdate().substring(0, 10))
                .as("Birthdate should have changed to the new value")
                .isEqualTo(newDate);

        softly.assertAll();
    }

    @Test
    @SkipAutoCleanup
    public void deletePersonVoidedTest() {
        CreatePersonRequest personRequest = AdminSteps.createPerson();
        CreatePersonResponse person = AdminSteps.createPerson(personRequest);
        String uuidForDelete = person.getUuid();

        new CrudRequester(
                RequestSpecs.adminSpec(),
                Endpoint.PERSON_DELETE,
                ResponseSpecs.requestReturnsNoContent())
                .delete(uuidForDelete);

        CreatePersonResponse personDataAfterDelete = AdminSteps.findPersonByUuid(uuidForDelete);
        assertThat(personDataAfterDelete.getVoided()).isEqualTo(true);
    }

    @Test
    @SkipAutoCleanup
    public void deletePersonPurgeTest() {
        CreatePersonRequest personRequest = AdminSteps.createPerson();
        CreatePersonResponse person = AdminSteps.createPerson(personRequest);
        String uuidForDelete = person.getUuid();

        new CrudRequester(
                RequestSpecs.adminSpec(),
                Endpoint.PERSON_DELETE,
                ResponseSpecs.requestReturnsNoContent())
                .delete(uuidForDelete, PATH_PARAM_PURGE);

        assertThat(AdminSteps.findDeletedPersonByUuidReturnsError(uuidForDelete).getError().getMessage()).isEqualTo(ResponseSpecs.OBJECT_WITH_GIVEN_UUID_DOES_NOT_EXIST);
    }
}
