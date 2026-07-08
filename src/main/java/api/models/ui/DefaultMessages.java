package api.models.ui;

import lombok.Getter;

@Getter
public enum DefaultMessages {
    SEARCH_INPUT_FIELD_DEFAULT_TEXT ("Search for a patient by name or identifier number"),
    SEARCH_NO_RESULTS_TITLE ("No recently viewed patients"),
    SEARCH_ACTIONS_TEXT ("Patients you select will appear here for quick access");
    private final String text;

    DefaultMessages(String message) {
        this.text = message;
    }
}
