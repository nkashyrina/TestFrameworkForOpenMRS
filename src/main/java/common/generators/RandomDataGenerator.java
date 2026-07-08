package common.generators;

import api.models.enums.Gender;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Random;
import java.util.UUID;

public final class RandomDataGenerator {
    private static final Random RANDOM = new Random();

    private RandomDataGenerator() {

    }

    public static String generateValidDate() {
        int year = 1900 + RANDOM.nextInt(126); // 1900-2025
        int month = 1 + RANDOM.nextInt(12);
        int day = 1 + RANDOM.nextInt(java.time.YearMonth.of(year, month).lengthOfMonth());
        return String.format("%04d-%02d-%02d", year, month, day);
    }

    public static String generateValidDateUiFormat() {
        int year = 1900 + RANDOM.nextInt(126); // 1900-2025
        int month = 1 + RANDOM.nextInt(12);
        int day = 1 + RANDOM.nextInt(java.time.YearMonth.of(year, month).lengthOfMonth());
        return String.format("%02d%02d%04d", day, month, year);
    }

    public static String randomString(int length) {
        String letters = "abcdefghijklmnopqrstuvwxyz";
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(letters.charAt(RANDOM.nextInt(letters.length())));
        }

        return sb.toString();
    }

    public static String generateName() {
        int minLength = 3;
        int maxLength = 15;
        int length = RandomUtils.secure().randomInt(minLength, maxLength + 1);

        return RandomStringUtils.secure().nextAlphabetic(length);
    }

    public static Gender randomGender() {
        Gender[] genders = Gender.values();

        return genders[RANDOM.nextInt(genders.length)];
    }

    public static Gender randomGender(String currentGender) {
        Gender[] genders = Gender.values();
        Gender newGender;
        do {
            newGender = genders[RANDOM.nextInt(genders.length)];
        } while (newGender.toString().equals(currentGender));

        return newGender;
    }

    public static int randomAge(int minAge, int maxAge) {
        return RANDOM.nextInt(maxAge - minAge + 1) + minAge;
    }

    public static String randomDateBetween(LocalDate from, LocalDate to) {
        long daysBetween = ChronoUnit.DAYS.between(from, to);
        LocalDate randomDate = from.plusDays(
                RANDOM.nextLong(daysBetween + 1));

        return randomDate.format(DateTimeFormatter.ISO_LOCAL_DATE); // produces in such format: "1990-06-15"
    }

    public static String generateVisitStartDatetime() {
        String dateStr = randomDateBetween(LocalDate.now(), LocalDate.now().plusDays(30));
        int hour = RANDOM.nextInt(20) + 4;
        return dateStr + String.format("T%02d:00:00.000Z", hour); // UTC
    }

    public static String generateVisitIndication() {
        return "API Test Visit " + UUID.randomUUID();
    }

    public static String getIncorrectPassword(){
        return RandomStringUtils.randomAlphanumeric(3).toUpperCase() +
                RandomStringUtils.randomAlphanumeric(3).toLowerCase() +
                RandomStringUtils.randomNumeric(2) + "!@#";
    }
}
