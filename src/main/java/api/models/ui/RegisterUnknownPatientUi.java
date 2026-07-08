package api.models.ui;

import api.models.Address;
import api.models.BaseModel;
import api.models.PersonAttributeForPatientCreation;
import api.models.PersonName;
import common.annotations.GeneratingRule;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegisterUnknownPatientUi extends BaseModel {
    private List<PersonName> names;
    @GeneratingRule(regex = "^(Male|Female|Unknown|Other)$")
    private String gender;
    @GeneratingRule(regex = "^([1-9]|[1-9]\\d|1[01]\\d|120)$")
    private Integer age;
    private String birthdate;
    @GeneratingRule(regex = "^true$")
    private Boolean birthdateEstimated;     // true if estimated
    private String birthTime;
    @GeneratingRule(regex = "^false$")
    private Boolean dead;
    private String deathDate;
    private String causeOfDeath;            // Concept UUID
    private Boolean deathdateEstimated;     // true if death date is estimated
    private List<Address> addresses;
    private List<PersonAttributeForPatientCreation> attributes;
}
