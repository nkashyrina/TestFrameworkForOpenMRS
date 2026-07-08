package api.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class IdentifierType {
    private String uuid;
//    private String display;
//    private String name;
//    private String description;
//    private String format;
//    private String formatDescription;
//    private boolean required;
//    private String validator;
//    private String locationBehavior;
//    private String uniquenessBehavior;
//    private boolean retired;
//    private AuditInfo auditInfo;
//    private List<Link> links;
//    private String resourceVersion;
}
