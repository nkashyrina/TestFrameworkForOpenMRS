package api.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class CreatePatientResponse extends BaseModel {
    private String uuid;
    private String display;
    private List<IdentifiersForPatientCreationResponse> identifiers;
    private CreatePersonResponse person;
    private boolean voided;
    private List<Link> links;
    private String resourceVersion;
}
