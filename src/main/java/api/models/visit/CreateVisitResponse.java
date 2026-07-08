package api.models.visit;

import api.models.BaseModel;
import api.models.Link;
import api.models.LocationResponse;
import api.models.PatientResponse;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class CreateVisitResponse extends BaseModel {
    private String uuid;
    private String display;
    private PatientResponse patient;
    private VisitTypeResponse visitType;
    private String startDatetime;
    private String stopDatetime;
    private LocationResponse location;
    private String indication;
    private List<VisitAttribute> attributes;
    private List<Object> encounters;
    private boolean voided;
    private List<Link> links;
    private String resourceVersion;
}
