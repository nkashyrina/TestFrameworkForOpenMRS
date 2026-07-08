package api.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IdentifiersForPatientCreation {
    private String identifier;
    private String identifierType;
    private boolean preferred;
    private String location;
}
