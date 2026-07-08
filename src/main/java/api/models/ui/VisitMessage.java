package api.models.ui;

public enum VisitMessage {
    START_VISIT_ERROR_REQUIRED(
            "required",
            "visit type",
            "mandatory",
            "select"
    ),

    NO_UPCOMING_APPOINTMENTS(
            "Upcoming appointments",
            "No upcoming appointments found"
    ),

    VISIT_DELETED_SUCCESSFULLY(
            "Facility Visit deleted",
            "successfully"
    );

    private final String[] expectedTexts;

    VisitMessage(String... expectedTexts) {
        this.expectedTexts = expectedTexts;
    }

    public String[] getExpectedTexts() {
        return expectedTexts;
    }

    public boolean isPresentIn(String actualText) {
        if (actualText == null) return false;
        String lowerText = actualText.toLowerCase();
        for (String expected : expectedTexts) {
            if (lowerText.contains(expected.toLowerCase())) {
                return true;
            }
        }
        return false;
    }
}
