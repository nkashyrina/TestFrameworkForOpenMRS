package api.requests;

import api.models.*;
import api.models.patient.UpdatePatientRequest;
import api.models.visit.CreateVisitRequest;
import api.models.visit.CreateVisitResponse;
import api.models.visit.VisitTypeResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum Endpoint {
    SESSION(
            "/session",
            BaseModel.class,
            RetrieveSessionResponse.class
    ),
    IDENTIFIER_SOURCE(
            "/idgen/identifiersource",
            BaseModel.class,
            IdentifierSource.class
    ),
    IDENTIFIER(
            "/idgen/identifiersource/{uuid}/identifier",
            BaseModel.class,
            IdentifierResponse.class
    ),
    LOCATION(
            "/location",
            BaseModel.class,
            LocationResponse.class
    ),
    PATIENT(
            "/patient/",
            CreatePatientRequest.class,
            CreatePatientResponse.class
    ),
    PATIENT_SEARCH(
            "/patient/{uuid}",
            BaseModel.class,
            CreatePatientResponse.class
    ),
    PATIENT_SEARCH_AFTER_DELETE(
            "/patient/{uuid}",
            BaseModel.class,
            ErrorResponse.class
    ),
    VISIT(
            "/visit",
            CreateVisitRequest.class,
            CreateVisitResponse.class
    ),
    VISIT_BY_UUID(
            "/visit/{uuid}",
            BaseModel.class,
            CreateVisitResponse.class
    ),
    VISIT_TYPE(
            "/visittype",
            BaseModel.class,
            VisitTypeResponse.class
    ),
    VISIT_TYPE_BY_UUID(
            "/visittype/{uuid}",
            BaseModel.class,
            VisitTypeResponse.class
    ),
    VISIT_DELETE(
            "/visit/{uuid}",
            BaseModel.class,
            BaseModel.class
    ),
    PATIENT_UPDATE(
            "/patient/{uuid}",
            UpdatePatientRequest.class,
            CreatePatientResponse.class
    ),
    PATIENT_DELETE(
            "/patient/{uuid}",
            CreatePatientRequest.class,
            ErrorResponse.class
    ),
    PERSON(
            "/person/",
            CreatePersonRequest.class,
            CreatePersonResponse.class
    ),
    PERSON_WITH_ERROR(
            "/person/",
            CreatePersonRequest.class,
            ErrorResponse.class
    ),
    PERSON_READ(
            "/person/{uuid}",
            BaseModel.class,
            CreatePersonResponse.class
    ),
    PERSON_READ_DELETED(
            "/person/{uuid}",
            BaseModel.class,
            ErrorResponse.class
    ),
    PERSON_UPDATE(
            "/person/{uuid}",
            CreatePersonRequest.class,
            CreatePersonResponse.class
    ),
    PERSON_DELETE(
            "/person/{uuid}",
            CreatePersonRequest.class,
            ErrorResponse.class
    ),
    PERSON_ADDRESS(
            "/person/{uuid}/address",
            BaseModel.class,
            AddressResponse.class
    );

    private final String url;
    private final Class<? extends BaseModel> requestModel;
    private final Class<? extends BaseModel> responseModel;

}
