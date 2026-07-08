package api.models.visit;

import api.models.AuditInfo;
import api.models.BaseModel;
import api.models.Link;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class VisitTypeResponse extends BaseModel {
    private String uuid;
    private String display;
    private String name;
    private String description;
    private boolean retired;
    private AuditInfo auditInfo;
    private List<Link> links;
    private String resourceVersion;
}
