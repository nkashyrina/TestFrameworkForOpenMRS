package api.models.ui;

import lombok.Getter;

import java.util.Random;

@Getter
public enum GenderUi {
    MALE("Male", "#omrs-icon-gender-male"),
    FEMALE("Female", "#omrs-icon-gender-female"),
    OTHER("Other", "#omrs-icon-gender-other"),
    UNKNOWN("Unknown", "#omrs-icon-gender-unknown");

    private final String gender;
    private final String iconId;

    GenderUi(String gender, String iconId) {
        this.gender = gender;
        this.iconId = iconId;
    }

    public static GenderUi getRandomGender() {
        final Random random = new Random();
        GenderUi[] genders = values();
        return genders[random.nextInt(genders.length)];
    }

    public static String toShortGender(String gender) {
        return gender.substring(0, 1);
    }
}
