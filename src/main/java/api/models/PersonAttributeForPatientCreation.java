package api.models;

import common.annotations.GeneratingRule;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PersonAttributeForPatientCreation {
    @GeneratingRule(regex = "^14d4f066-15f5-102d-96e4-000c29c2a5d7$")
    private String attributeType;
    @GeneratingRule(regex = "^(?:\\+?1[-. ]?)?\\(?([0-9]{3})\\)?[-. ]?([0-9]{3})[-. ]?([0-9]{4})$")
    private String value;
}
