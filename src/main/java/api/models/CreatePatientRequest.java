package api.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreatePatientRequest extends BaseModel {
    private List<IdentifiersForPatientCreation> identifiers;
    private CreatePersonRequest person;

}
