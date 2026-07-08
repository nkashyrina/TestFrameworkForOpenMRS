package api.assertions;

import api.models.CreatePatientResponse;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class CommonAssertions {
    public static void assertFieldContainText(List<CreatePatientResponse> results, String searchText) {
        String[] searchWords = searchText.toLowerCase().replace("+", " ").trim().split("\\s+");
        for (CreatePatientResponse person : results) {
            String display = person.getDisplay().toLowerCase();
            for (String word : searchWords) {
                assertThat(display).contains(word);
            }
        }
    }
}
