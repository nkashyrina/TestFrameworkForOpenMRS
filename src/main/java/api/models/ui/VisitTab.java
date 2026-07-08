package api.models.ui;

import lombok.Getter;

@Getter
public enum VisitTab {
    NEW("new", "New visit tab"),
    ONGOING("ongoing", "Ongoing visit tab"),
    IN_THE_PAST("in the past", "Past visit tab");

    private final String tabName;
    private final String description;

    VisitTab(String tabName, String description) {
        this.tabName = tabName;
        this.description = description;
    }
}
