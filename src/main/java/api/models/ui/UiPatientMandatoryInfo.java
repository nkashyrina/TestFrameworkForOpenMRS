package api.models.ui;

import api.models.BaseModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UiPatientMandatoryInfo extends BaseModel{
    private String openMRSuuid;
    private String names;
    private String gender;
    private Integer age;                    // estimated age in years
    private String birthdate;
    private Boolean birthdateEstimated;     // true if estimated

    /*private Boolean dead;                   // true if patient is dead
        private String deathDate;               // date of death
        private String causeOfDeath;            // Concept UUID
        private Boolean deathdateEstimated;     // true if death date is estimated
        private List<Address> addresses;     // addresses array
        private List<PersonAttributeForPatientCreation> attributes;  // attributes array*/

}
