package api.models.patient;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IdentifiersForPatientUpdate {
    private String uuid;
    private String identifier;
    private String identifierType;
    private boolean preferred;
    private String location;
}
