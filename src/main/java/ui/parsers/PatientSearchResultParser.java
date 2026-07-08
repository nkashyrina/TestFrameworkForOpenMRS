package ui.parsers;

import api.models.ui.UiPatientMandatoryInfo;
import com.codeborne.selenide.SelenideElement;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class PatientSearchResultParser {
    public UiPatientMandatoryInfo parse(SelenideElement el) {
        List<String> lines = extractLines(el);
        String ageLine = findAge(lines);

        return UiPatientMandatoryInfo.builder()
                .names(findName(lines))
                .gender(findGender(lines))
                .age(parseAge(ageLine))
                .birthdate(findBirthdate(lines))
                .birthdateEstimated(isEstimated(ageLine))
                .openMRSuuid(findId(lines))
                .build();
    }

    private List<String> extractLines(SelenideElement el) {
        return Arrays.stream(el.getText().split("\n"))
                .map(String::trim)
                .filter(line -> !line.isEmpty() && !line.equals("·"))
                .collect(Collectors.toList());
    }

    private String findName(List<String> lines) {
        return lines.stream()
                .filter(l -> l.matches("[A-Za-z]+(\\s[A-Za-z]+)+"))
                .findFirst()
                .orElse(null);
    }

    private String findGender(List<String> lines) {
        return lines.stream()
                .filter(l -> l.matches("(?i)Male|Female|Unknown|Other"))
                .findFirst()
                .orElse(null);
    }

    private String findAge(List<String> lines) {
        return lines.stream()
                .filter(l -> l.matches("\\d+.*") && !l.matches("\\d{2}-[A-Za-z]{3}-\\d{4}"))
                .findFirst()
                .orElse(null);
    }

    private String findBirthdate(List<String> lines) {
        return lines.stream()
                .filter(l -> l.matches("\\d{2}-[A-Za-z]{3}-\\d{4}"))
                .findFirst()
                .orElse(null);
    }

    private String findId(List<String> lines) {
        return lines.stream()
                .filter(l -> l.contains("OpenMRS ID"))
                .map(l -> l.replace("OpenMRS ID:", "").trim())
                .findFirst()
                .orElse(null);
    }

    private Integer parseAge(String ageLine) {
        if (ageLine == null) return null;
        if (ageLine.contains("yrs")) {
            return Integer.parseInt(ageLine.split("yrs")[0].trim());
        }
        return 0;
    }

    private Boolean isEstimated(String ageLine) {
        if (ageLine == null) return false;
        return ageLine.contains("wks") || ageLine.contains("days");
    }

}
