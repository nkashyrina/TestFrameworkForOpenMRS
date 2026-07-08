package common.utils;

import java.time.LocalDate;
import java.time.Period;
import java.time.YearMonth;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public final class DateUtils {
    private DateUtils() {

    }

    public static int calculateAge(String birthDate) {
        LocalDate birth = LocalDate.parse(birthDate, DateTimeFormatter.ISO_LOCAL_DATE);
        LocalDate now = LocalDate.now();
        return Period.between(birth, now).getYears();
    }

    public static String convertDdMmYyyyToDdMmmYyyy(String dateInDdmmyyyy) {
        try {
            DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("ddMMyyyy");
            DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("dd-MMM-yyyy", java.util.Locale.ENGLISH);

            LocalDate date = LocalDate.parse(dateInDdmmyyyy, inputFormatter);
            String result = date.format(outputFormatter);

            result = result.replace("Sep", "Sept");

            return result;
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Incorrect date format: " + dateInDdmmyyyy + ". Expected ddMMyyyy", e);
        }
    }

    public static String convertAgeToMmmYyyy(Integer age) {
        try {
            LocalDate birthDate = LocalDate.now()
                    .minusYears(age)
                    .withDayOfMonth(1);

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM yyyy", java.util.Locale.ENGLISH);
            String result = birthDate.format(formatter);

            result = result.replace("Sep", "Sept");

            return result;
        } catch (Exception e) {
            throw new IllegalArgumentException("Incorrect age: " + age, e);
        }
    }

    public static String convertMmmYyyyToFullDate(String mmmYyyy) {
        try {
            DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("MMM yyyy", java.util.Locale.ENGLISH);

            YearMonth yearMonth = YearMonth.parse(mmmYyyy.replace("Sept", "Sep"), inputFormatter);

            LocalDate date = yearMonth.atDay(LocalDate.now().getDayOfMonth());

            return date.atStartOfDay(ZoneOffset.UTC)
                    .format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZ"));
        } catch (Exception e) {
            throw new IllegalArgumentException("Incorrect date format: " + mmmYyyy, e);
        }
    }
}
