package api.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import common.annotations.GeneratingRule;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CreatePersonRequest extends BaseModel {
    private List<PersonName> names;
    @GeneratingRule(regex = "^[MUF]$")
    private String gender;                  // "M", "F", "U"
    private Integer age;                    // estimated age in years
    private String birthdate;               // date of birth (string format, e.g., "YYYY-MM-DD")
    @GeneratingRule(regex = "^(true|false)$")
    private Boolean birthdateEstimated;     // true if estimated
    private String birthTime;               // time of birth
    @GeneratingRule(regex = "^false$")
    private Boolean dead;                   // true if patient is dead
    private String deathDate;               // date of death
    private String causeOfDeath;            // Concept UUID
    private Boolean deathdateEstimated;     // true if death date is estimated
    private List<Address> addresses;     // addresses array
    private List<PersonAttributeForPatientCreation> attributes;  // attributes array
}
