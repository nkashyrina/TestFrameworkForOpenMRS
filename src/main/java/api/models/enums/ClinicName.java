package api.models.enums;

import lombok.Getter;

@Getter
public enum ClinicName {
    OUTPATIENT("Outpatient");


    ClinicName(String clinicName) {
        this.clinicName = clinicName;
    }

    private final String clinicName;
}
