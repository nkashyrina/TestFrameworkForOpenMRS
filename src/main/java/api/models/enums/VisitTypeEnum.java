package api.models.enums;

public enum VisitTypeEnum {
    FACILITY_VISIT("Facility Visit"),
    HOME_VISIT("Home Visit"),
    OPD_VISIT("OPD Visit"),
    OFFLINE_VISIT("Offline Visit"),
    GROUP_SESSION("Group Session");

    private final String displayName;

    VisitTypeEnum(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
