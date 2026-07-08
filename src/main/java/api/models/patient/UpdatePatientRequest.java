package api.models.patient;

import api.models.BaseModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdatePatientRequest extends BaseModel {
    private List<IdentifiersForPatientUpdate> identifiers;
    private PersonForPatientUpdate person;
}
