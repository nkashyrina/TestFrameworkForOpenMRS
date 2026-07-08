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
public class RegisterMandatoryFieldsPatientUi extends BaseModel {
    private List<PersonName> names;
    @GeneratingRule(regex = "^(Male|Female|Unknown|Other)$")
    private String gender;
    private String birthdate;
    @GeneratingRule(regex = "^false$")
    private Boolean birthdateEstimated;     // true if estimated
    @GeneratingRule(regex = "^false$")
    private Boolean dead;                   // true if patient is dead
    private String deathDate;               // date of death
    private String causeOfDeath;            // Concept UUID
    private Boolean deathdateEstimated;     // true if death date is estimated
    private List<Address> addresses;     // addresses array
    private List<PersonAttributeForPatientCreation> attributes;  // attributes array
}

