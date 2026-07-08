package api.models.visit;

import api.models.BaseModel;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class VisitAttributeType extends BaseModel {
    private String name;
    private String description;
    private String datatypeClassname;
    private Integer minOccurs;
    private Integer maxOccurs;
    private String datatypeConfig;
}
