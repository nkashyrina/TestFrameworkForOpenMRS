package api.models.patient;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PersonNameForPatientUpdate {
    private String uuid;
    private String givenName;
    private String middleName;
    private String familyName;
    private Boolean preferred;
}
