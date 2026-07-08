package api.utils;

import api.models.IdentifiersForPatientCreation;
import api.models.PersonName;
import api.models.patient.PersonNameForPatientUpdate;

public final class DisplayFormatterUtils {
    private DisplayFormatterUtils() {

    }

    public static String personDisplayFormatter(PersonName name) {
        if (name.getMiddleName() == null || name.getMiddleName().isEmpty()) {
            return String.format("%s %s", name.getGivenName(), name.getFamilyName());
        } else if (name.getFamilyName().isEmpty()) {
            return String.format("%s %s", name.getGivenName(), name.getMiddleName());
        }

        return String.format("%s %s %s",
                name.getGivenName(), name.getMiddleName(), name.getFamilyName());
    }

    public static String personDisplayFormatter(PersonNameForPatientUpdate name) {
        if (name.getMiddleName().isEmpty()) {
            return String.format("%s %s", name.getGivenName(), name.getFamilyName());
        } else if (name.getFamilyName().isEmpty()) {
            return String.format("%s %s", name.getGivenName(), name.getMiddleName());
        }

        return String.format("%s %s %s",
                name.getGivenName(), name.getMiddleName(), name.getFamilyName());
    }

    private static String patientDisplay(IdentifiersForPatientCreation identifiers, PersonName name) {
        return String.format("%s - %s", identifiers.getIdentifier(), personDisplayFormatter(name));
    }
}
