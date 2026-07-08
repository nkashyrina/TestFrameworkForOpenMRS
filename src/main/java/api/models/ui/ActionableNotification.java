package api.models.ui;

import lombok.Getter;

@Getter
public enum ActionableNotification {
    NEW_PATIENT_CREATED("New Patient Created", "The patient can now be found by searching for them using their name or ID number");

    ActionableNotification(String notificationTitle, String notificationSubTitle) {
        this.notificationTitle = notificationTitle;
        this.notificationSubTitle = notificationSubTitle;
    }

    private final String notificationTitle;
    private final String notificationSubTitle;
}
