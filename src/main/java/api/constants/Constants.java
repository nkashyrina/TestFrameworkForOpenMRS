package api.constants;

public final class Constants {
    public static String[] personFieldsToBeGenerated = new String[]{"gender", "birthdate", "birthdateEstimated",
            "dead", "addresses", "attributes"};
    public static String[] nameFieldsToBeGenerated = new String[]{"givenName", "middleName", "familyName"};
    public static final boolean PREFERRED_IDENTIFIER_TRUE = true;
    public static final Boolean PATH_PARAM_PURGE = true;

    public static final String IDENTIFIER_SOURCE_UUID = "8549f706-7e85-4c1d-9424-217d50a2988b";
    public static final String IDENTIFIER_TYPE_UUID = "05a29f94-c0ed-11e2-94be-8c13b969e334";

    public static final String CLINIC_NAME = "Clinic";

    private Constants() {
    }
}
