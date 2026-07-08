package api.models;

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
public class PersonName {
    private String givenName;
    private String middleName;
    private String familyName;
    private String familyName2;
    private Boolean preferred;
    private String prefix;
    private String familyNamePrefix;
    private String familyNameSuffix;
    private String degree;
}
