package api.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class IdentifierSource extends BaseModel {
    private String uuid;
    private IdentifierType identifierType;
}
