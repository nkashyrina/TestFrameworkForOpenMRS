package api.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class IdentifiersForPatientCreationResponse {
    private String uuid;
    private String display;
    private List<Link> links;
}
